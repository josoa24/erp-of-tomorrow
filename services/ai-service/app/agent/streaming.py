"""Mode streaming de l'agent : mêmes étapes que run(), mais le texte est émis au fil de l'eau."""
from __future__ import annotations

import logging
import time
from typing import Any, AsyncIterator

from google.genai import types

from app.agent.agent_base import AgentBase
from app.agent.confirmation import ActionGuard
from app.agent.gemini_errors import to_agent_error
from app.agent.records import ToolCallRecord
from app.agent.ui_blocks import blocks_for
from app.trust.assessment import assess

logger = logging.getLogger(__name__)


class StreamingAgent(AgentBase):
    async def _stream_gemini(self, contents: list[types.Content], config: types.GenerateContentConfig,
                             turn: int, metrics: dict[str, Any], acc: dict[str, Any]) -> AsyncIterator[str]:
        """Itère sur un appel Gemini en streaming : émet les morceaux de texte, accumule les parts dans `acc`."""
        start = time.perf_counter()
        first_chunk_ms: float | None = None
        usage = None
        try:
            stream = await self._client.aio.models.generate_content_stream(
                model=self._settings.gemini_model, contents=contents, config=config)
            async for chunk in stream:
                if first_chunk_ms is None:
                    first_chunk_ms = (time.perf_counter() - start) * 1000
                usage = chunk.usage_metadata or usage
                if chunk.prompt_feedback and chunk.prompt_feedback.block_reason:
                    acc["blocked"] = f"Demande bloquée par Gemini ({chunk.prompt_feedback.block_reason})."
                cand = chunk.candidates[0] if chunk.candidates else None
                if cand is None or cand.content is None or not cand.content.parts:
                    continue
                for part in cand.content.parts:
                    # On garde TOUTES les parts (texte, function_call, thought_signature) pour l'historique.
                    acc["parts"].append(part)
                    if part.function_call:
                        acc["calls"].append(part.function_call)
                    elif part.text and not part.thought:
                        acc["text"].append(part.text)
                        yield part.text
        except Exception as exc:  # noqa: BLE001
            raise to_agent_error(exc) from None
        metrics["gemini_calls"].append(self._usage(
            turn, (time.perf_counter() - start) * 1000, usage,
            first_chunk_ms=round(first_chunk_ms, 1) if first_chunk_ms is not None else None))

    async def run_stream(self, message: str, history: list[types.Content] | None = None,
                         guard: ActionGuard | None = None) -> AsyncIterator[dict[str, Any]]:
        """Génère des événements : token, tool_call, tool_result, puis done (ou une AgentError est levée)."""
        t0 = time.perf_counter()
        contents, metrics = self._start(message, history)
        records: list[ToolCallRecord] = []
        config_auto, config_none = self._configs.for_request()
        max_turns = self._settings.max_tool_turns
        streamed: list[str] = []
        first_token_ms: float | None = None
        reached_limit = False

        for turn in range(1, max_turns + 2):
            forced = turn > max_turns
            if forced:
                reached_limit = True
                logger.warning("Limite de %d tours atteinte, réponse finale forcée.", max_turns)
            acc: dict[str, Any] = {"parts": [], "calls": [], "text": [], "blocked": None}
            async for delta in self._stream_gemini(contents, config_none if forced else config_auto,
                                                   turn, metrics, acc):
                if first_token_ms is None:
                    first_token_ms = (time.perf_counter() - t0) * 1000
                streamed.append(delta)
                yield {"type": "token", "text": delta}

            if acc["calls"] and not forced:
                contents.append(types.Content(role="model", parts=acc["parts"]))  # signatures conservées
                for call in acc["calls"]:
                    yield {"type": "tool_call", "turn": turn, "name": call.name, "arguments": dict(call.args or {})}
                n_before = len(records)
                response_parts = await self._run_tools(acc["calls"], turn, records, metrics, guard)
                contents.append(types.Content(role="user", parts=response_parts))
                for rec in records[n_before:]:
                    yield {"type": "tool_result", **rec.as_dict()}
                    blocks = blocks_for(rec)
                    if blocks:
                        yield {"type": "ui", "tool": rec.name, "blocks": blocks}
                continue

            if not acc["text"]:
                reason = acc["blocked"] or "Réponse vide de Gemini."
                streamed.append(reason)
                yield {"type": "token", "text": reason}
            break

        total = self._finish_metrics(metrics, t0, first_token_ms=round(first_token_ms, 1)
                                     if first_token_ms is not None else None)
        logger.info("Stream terminé en %.0f ms (1er token à %s ms)", total, metrics["first_token_ms"])
        response = "".join(streamed).strip()
        yield {"type": "done", "response": response, "tool_calls": [r.as_dict() for r in records],
               "max_turns_reached": reached_limit, "metrics": metrics, "trust": assess(message, response, records)}
