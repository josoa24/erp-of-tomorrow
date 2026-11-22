"""Orchestrateur de l'agent : boucle Gemini Function Calling natif.

Cycle d'un tour :
    user -> Gemini (avec outils) -> function_calls ?
        oui : exécution (en parallèle) -> FunctionResponse -> Gemini ...
        non : texte final -> réponse structurée (+ étiquette de confiance)

Le choix des outils est fait uniquement par Gemini (aucun mot-clé).
L'historique `contents` est conservé intégralement pendant la boucle, et
le contenu renvoyé par le modèle est réinjecté tel quel pour préserver
les thought signatures (obligatoires avec Gemini 3).
Le mode streaming est dans app/agent/streaming.py.
"""
from __future__ import annotations

import logging
import time
from typing import Any

from google.genai import types

from app.agent.confirmation import ActionGuard
from app.agent.gemini_errors import to_agent_error
from app.agent.records import AgentResult, ToolCallRecord
from app.agent.streaming import StreamingAgent
from app.trust.assessment import assess

logger = logging.getLogger(__name__)


def _model_content(resp: types.GenerateContentResponse) -> types.Content | None:
    if resp.candidates and resp.candidates[0].content and resp.candidates[0].content.parts:
        return resp.candidates[0].content
    return None


def _empty_reason(resp: types.GenerateContentResponse) -> str:
    if resp.prompt_feedback and resp.prompt_feedback.block_reason:
        return f"Demande bloquée par Gemini ({resp.prompt_feedback.block_reason})."
    if resp.candidates and resp.candidates[0].finish_reason:
        return f"Réponse vide de Gemini (finish_reason={resp.candidates[0].finish_reason})."
    return "Réponse vide de Gemini."


class GeminiAgent(StreamingAgent):
    async def _call_gemini(self, contents: list[types.Content], config: types.GenerateContentConfig,
                           turn: int, metrics: dict[str, Any]) -> types.GenerateContentResponse:
        start = time.perf_counter()
        try:
            resp = await self._client.aio.models.generate_content(
                model=self._settings.gemini_model, contents=contents, config=config)
        except Exception as exc:  # noqa: BLE001
            raise to_agent_error(exc) from None
        elapsed = (time.perf_counter() - start) * 1000
        metrics["gemini_calls"].append(self._usage(turn, elapsed, resp.usage_metadata))
        logger.info("Gemini tour %d : %.0f ms", turn, elapsed)
        return resp

    async def run(self, message: str, history: list[types.Content] | None = None,
                  guard: ActionGuard | None = None) -> AgentResult:
        """`history` : échanges précédents (texte user/model) de la conversation, déjà compactés."""
        t0 = time.perf_counter()
        contents, metrics = self._start(message, history)
        records: list[ToolCallRecord] = []
        max_turns = self._settings.max_tool_turns
        final_text: str | None = None
        reached_limit = False
        # Instruction système recalculée à chaque requête (date du jour), configs réutilisées.
        config_auto, config_none = self._configs.for_request()

        for turn in range(1, max_turns + 1):
            resp = await self._call_gemini(contents, config_auto, turn, metrics)
            model_content = _model_content(resp)
            if model_content is None:
                final_text = _empty_reason(resp)
                break
            calls = resp.function_calls or []
            if not calls:
                final_text = (resp.text or "").strip() or _empty_reason(resp)
                break
            contents.append(model_content)  # conserve function calls + thought signatures
            response_parts = await self._run_tools(calls, turn, records, metrics, guard)
            contents.append(types.Content(role="user", parts=response_parts))
        else:
            # Limite de tours atteinte : un dernier appel SANS outils pour forcer une réponse texte.
            reached_limit = True
            logger.warning("Limite de %d tours atteinte, réponse finale forcée.", max_turns)
            resp = await self._call_gemini(contents, config_none, max_turns + 1, metrics)
            final_text = (resp.text or "").strip() or _empty_reason(resp)

        total = self._finish_metrics(metrics, t0)
        logger.info("Requête traitée en %.0f ms (%d appels Gemini, %d outils)",
                    total, len(metrics["gemini_calls"]), len(records))
        response = final_text or ""
        return AgentResult(response=response, tool_calls=records, metrics=metrics,
                           max_turns_reached=reached_limit, trust=assess(message, response, records))
