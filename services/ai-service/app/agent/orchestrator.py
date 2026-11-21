"""Orchestrateur de l'agent : boucle Gemini Function Calling natif.

Cycle d'un tour :
    user -> Gemini (avec outils) -> function_calls ?
        oui : exécution (en parallèle) -> FunctionResponse -> Gemini ...
        non : texte final -> réponse structurée

Le choix des outils est fait uniquement par Gemini (aucun mot-clé).
L'historique `contents` est conservé intégralement pendant la boucle, et
le contenu renvoyé par le modèle est réinjecté tel quel pour préserver
les thought signatures (obligatoires avec Gemini 3).
"""
from __future__ import annotations

import asyncio
import logging
import time
from dataclasses import dataclass, field
from typing import Any, AsyncIterator

from google import genai
from google.genai import errors as genai_errors
from google.genai import types

from app.agent.confirmation import ActionGuard
from app.agent.prompts import build_system_instruction
from app.erp.context import current_user
from app.config import Settings
from app.tools.registry import ToolRegistry

logger = logging.getLogger(__name__)

RETRYABLE_STATUS = [429, 500, 502, 503, 504]


# --------------------------------------------------------------------------- #
# Résultats
# --------------------------------------------------------------------------- #
@dataclass
class ToolCallRecord:
    name: str
    arguments: dict[str, Any]
    success: bool
    duration_ms: float
    turn: int
    error_code: str | None = None
    # Saisies : "pending_confirmation" (proposée), "executed" (confirmée), "cancelled"
    status: str | None = None
    action: dict[str, Any] | None = None

    def as_dict(self) -> dict[str, Any]:
        d = {"name": self.name, "arguments": self.arguments, "success": self.success,
             "duration_ms": round(self.duration_ms, 1), "turn": self.turn}
        if self.error_code:
            d["error_code"] = self.error_code
        if self.status:
            d["status"] = self.status
        if self.action:
            d["action"] = self.action
        return d


@dataclass
class AgentResult:
    response: str
    tool_calls: list[ToolCallRecord] = field(default_factory=list)
    metrics: dict[str, Any] = field(default_factory=dict)
    max_turns_reached: bool = False


class AgentError(Exception):
    """Erreur côté Gemini, sans aucune donnée sensible."""

    def __init__(self, status_code: int, code: str, message: str) -> None:
        super().__init__(message)
        self.status_code = status_code
        self.code = code
        self.message = message


# --------------------------------------------------------------------------- #
# Client Gemini
# --------------------------------------------------------------------------- #
def build_client(settings: Settings) -> genai.Client:
    return genai.Client(
        api_key=settings.gemini_api_key.get_secret_value(),
        http_options=types.HttpOptions(
            timeout=int(settings.gemini_timeout_s * 1000),  # en millisecondes
            retry_options=types.HttpRetryOptions(
                attempts=settings.gemini_retry_attempts,
                initial_delay=0.5, max_delay=4.0,
                http_status_codes=RETRYABLE_STATUS,
            ),
        ),
    )


async def check_model(client: genai.Client, model: str) -> dict[str, Any]:
    """Vérifie que le modèle existe et accepte generateContent (donc le function calling)."""
    try:
        info = await client.aio.models.get(model=model)
    except genai_errors.APIError as exc:
        return {"available": False, "model": model, "error": f"{exc.code} {exc.status}: {exc.message}"}
    except Exception as exc:  # réseau, timeout...
        return {"available": False, "model": model, "error": type(exc).__name__}
    actions = list(info.supported_actions or [])
    return {
        "available": True,
        "model": model,
        "display_name": info.display_name,
        "supports_generate_content": (not actions) or "generateContent" in actions,
        "input_token_limit": info.input_token_limit,
    }


def _is_timeout(exc: BaseException) -> bool:
    while exc is not None:
        if isinstance(exc, (TimeoutError, asyncio.TimeoutError)) or "Timeout" in type(exc).__name__:
            return True
        exc = exc.__cause__ or exc.__context__  # type: ignore[assignment]
    return False


def _to_agent_error(exc: Exception) -> AgentError:
    """Convertit toute erreur d'appel Gemini (API, timeout, réseau) en AgentError sans secret."""
    if isinstance(exc, AgentError):
        return exc
    if isinstance(exc, genai_errors.APIError):
        logger.warning("Erreur Gemini %s %s", exc.code, exc.status)
        return _map_api_error(exc)
    if _is_timeout(exc):
        return AgentError(504, "GEMINI_TIMEOUT", "Gemini n'a pas répondu à temps.")
    logger.warning("Erreur réseau Gemini : %s", type(exc).__name__)
    return AgentError(502, "GEMINI_NETWORK_ERROR", f"Erreur réseau vers Gemini ({type(exc).__name__}).")


def _map_api_error(exc: genai_errors.APIError) -> AgentError:
    code = exc.code or 0
    msg = (exc.message or "").strip()[:300]
    if code == 429:
        return AgentError(503, "GEMINI_RATE_LIMIT", "Quota Gemini atteint, réessayez dans un instant.")
    if code == 404:
        return AgentError(502, "GEMINI_MODEL_NOT_FOUND", f"Modèle Gemini introuvable ou indisponible : {msg}")
    if code in (401, 403) or "API key" in msg:
        return AgentError(502, "GEMINI_AUTH_ERROR", "Clé API Gemini invalide ou non autorisée.")
    if 400 <= code < 500:
        return AgentError(502, "GEMINI_BAD_REQUEST", f"Requête refusée par Gemini ({code}) : {msg}")
    return AgentError(502, "GEMINI_SERVER_ERROR", f"Erreur serveur Gemini ({code}).")


# --------------------------------------------------------------------------- #
# Agent
# --------------------------------------------------------------------------- #
class GeminiAgent:
    def __init__(self, client: genai.Client, registry: ToolRegistry, settings: Settings) -> None:
        self._client = client
        self._registry = registry
        self._settings = settings
        self._configs: dict[str, tuple[types.GenerateContentConfig, types.GenerateContentConfig]] = {}

    def _configs_for_request(self) -> tuple[types.GenerateContentConfig, types.GenerateContentConfig]:
        """Configs (outils autorisés pour le rôle + instruction système du jour), mises en cache par rôle."""
        user = current_user.get()
        role = user.role if user else None
        key = role or "-"
        if key not in self._configs:
            self._configs[key] = (self._build_config(types.FunctionCallingConfigMode.AUTO, role),
                                  self._build_config(types.FunctionCallingConfigMode.NONE, role))
        system = build_system_instruction(user=user)
        auto, none = self._configs[key]
        return (auto.model_copy(update={"system_instruction": system}),
                none.model_copy(update={"system_instruction": system}))

    def _build_config(self, mode: types.FunctionCallingConfigMode, role: str | None) -> types.GenerateContentConfig:
        s = self._settings
        thinking = None
        if s.gemini_thinking_level:
            thinking = types.ThinkingConfig(thinking_level=s.gemini_thinking_level.upper())
        elif s.gemini_thinking_budget is not None:
            thinking = types.ThinkingConfig(thinking_budget=s.gemini_thinking_budget)
        return types.GenerateContentConfig(
            tools=[self._registry.gemini_tool(role)],
            tool_config=types.ToolConfig(function_calling_config=types.FunctionCallingConfig(mode=mode)),
            # On exécute nous-mêmes les outils : pas de function calling automatique du SDK.
            automatic_function_calling=types.AutomaticFunctionCallingConfig(disable=True),
            thinking_config=thinking,
            temperature=s.gemini_temperature,
        )

    async def _call_gemini(self, contents: list[types.Content], config: types.GenerateContentConfig,
                           turn: int, metrics: dict[str, Any]) -> types.GenerateContentResponse:
        start = time.perf_counter()
        try:
            resp = await self._client.aio.models.generate_content(
                model=self._settings.gemini_model, contents=contents, config=config)
        except Exception as exc:  # noqa: BLE001
            raise _to_agent_error(exc) from None
        elapsed = (time.perf_counter() - start) * 1000
        usage = resp.usage_metadata
        metrics["gemini_calls"].append({
            "turn": turn,
            "duration_ms": round(elapsed, 1),
            "prompt_tokens": getattr(usage, "prompt_token_count", None),
            "output_tokens": getattr(usage, "candidates_token_count", None),
            "thinking_tokens": getattr(usage, "thoughts_token_count", None),
        })
        logger.info("Gemini tour %d : %.0f ms", turn, elapsed)
        return resp

    @staticmethod
    def _model_content(resp: types.GenerateContentResponse) -> types.Content | None:
        if resp.candidates and resp.candidates[0].content and resp.candidates[0].content.parts:
            return resp.candidates[0].content
        return None

    @staticmethod
    def _empty_reason(resp: types.GenerateContentResponse) -> str:
        if resp.prompt_feedback and resp.prompt_feedback.block_reason:
            return f"Demande bloquée par Gemini ({resp.prompt_feedback.block_reason})."
        if resp.candidates and resp.candidates[0].finish_reason:
            return f"Réponse vide de Gemini (finish_reason={resp.candidates[0].finish_reason})."
        return "Réponse vide de Gemini."

    @staticmethod
    def _record(call: types.FunctionCall, result: dict[str, Any], duration: float, turn: int) -> ToolCallRecord:
        ok = bool(result.get("success", False))
        status, action = None, None
        if result.get("status") == "CONFIRMATION_REQUIRED":
            status = "pending_confirmation"
            action = {k: result[k] for k in ("action_id", "tool", "arguments", "summary")}
        elif result.get("status") == "CANCELLED":
            status, action = "cancelled", {k: result[k] for k in ("action_id", "tool", "summary")}
        elif "confirmed_action" in result:
            status = "executed" if ok else "failed"
            action = result["confirmed_action"]
        return ToolCallRecord(
            name=call.name, arguments=dict(call.args or {}), success=ok, duration_ms=duration, turn=turn,
            error_code=None if ok else result.get("error", {}).get("code"), status=status, action=action)

    async def _run_tools(self, calls: list[types.FunctionCall], turn: int, records: list[ToolCallRecord],
                         metrics: dict[str, Any], guard: ActionGuard | None = None) -> list[types.Part]:
        """Exécute tous les appels d'un même tour en parallèle (ils sont indépendants)."""
        start = time.perf_counter()
        outcomes = await asyncio.gather(*(self._registry.execute_timed(c.name, c.args, guard) for c in calls))
        metrics["tools_ms"] += (time.perf_counter() - start) * 1000

        parts: list[types.Part] = []
        for call, (result, duration) in zip(calls, outcomes):
            records.append(self._record(call, result, duration, turn))
            # Un FunctionResponse par FunctionCall, même nom et même id, dans le même ordre.
            parts.append(types.Part(function_response=types.FunctionResponse(
                id=call.id, name=call.name, response=result)))
        return parts

    async def run(self, message: str, history: list[types.Content] | None = None,
                  guard: ActionGuard | None = None) -> AgentResult:
        """`history` : échanges précédents (texte user/model) de la conversation, déjà compactés."""
        t0 = time.perf_counter()
        metrics: dict[str, Any] = {"gemini_calls": [], "tools_ms": 0.0,
                                   "history_messages": len(history or [])}
        records: list[ToolCallRecord] = []
        contents: list[types.Content] = list(history or [])
        contents.append(types.Content(role="user", parts=[types.Part.from_text(text=message)]))
        max_turns = self._settings.max_tool_turns
        final_text: str | None = None
        reached_limit = False

        # Instruction système recalculée à chaque requête (date du jour), configs réutilisées.
        config_auto, config_none = self._configs_for_request()

        for turn in range(1, max_turns + 1):
            resp = await self._call_gemini(contents, config_auto, turn, metrics)
            model_content = self._model_content(resp)
            if model_content is None:
                final_text = self._empty_reason(resp)
                break
            calls = resp.function_calls or []
            if not calls:
                final_text = (resp.text or "").strip() or self._empty_reason(resp)
                break
            contents.append(model_content)  # conserve function calls + thought signatures
            response_parts = await self._run_tools(calls, turn, records, metrics, guard)
            contents.append(types.Content(role="user", parts=response_parts))
        else:
            # Limite de tours atteinte : un dernier appel SANS outils pour forcer une réponse texte.
            reached_limit = True
            logger.warning("Limite de %d tours atteinte, réponse finale forcée.", max_turns)
            resp = await self._call_gemini(contents, config_none, max_turns + 1, metrics)
            final_text = (resp.text or "").strip() or self._empty_reason(resp)

        total = (time.perf_counter() - t0) * 1000
        gemini_ms = sum(c["duration_ms"] for c in metrics["gemini_calls"])
        metrics.update({
            "total_ms": round(total, 1),
            "gemini_ms": round(gemini_ms, 1),
            "tools_ms": round(metrics["tools_ms"], 1),
            "overhead_ms": round(total - gemini_ms - metrics["tools_ms"], 1),
            "gemini_call_count": len(metrics["gemini_calls"]),
            "model": self._settings.gemini_model,
        })
        logger.info("Requête traitée en %.0f ms (%d appels Gemini, %d outils)",
                    total, len(metrics["gemini_calls"]), len(records))
        return AgentResult(response=final_text or "", tool_calls=records,
                           metrics=metrics, max_turns_reached=reached_limit)

    # ------------------------------------------------------------------ #
    # Streaming : mêmes étapes que run(), mais le texte est émis au fil de l'eau.
    # ------------------------------------------------------------------ #
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
            raise _to_agent_error(exc) from None
        elapsed = (time.perf_counter() - start) * 1000
        metrics["gemini_calls"].append({
            "turn": turn,
            "duration_ms": round(elapsed, 1),
            "first_chunk_ms": round(first_chunk_ms, 1) if first_chunk_ms is not None else None,
            "prompt_tokens": getattr(usage, "prompt_token_count", None),
            "output_tokens": getattr(usage, "candidates_token_count", None),
            "thinking_tokens": getattr(usage, "thoughts_token_count", None),
        })

    async def run_stream(self, message: str, history: list[types.Content] | None = None,
                         guard: ActionGuard | None = None) -> AsyncIterator[dict[str, Any]]:
        """Génère des événements : token, tool_call, tool_result, puis done (ou une AgentError est levée)."""
        t0 = time.perf_counter()
        metrics: dict[str, Any] = {"gemini_calls": [], "tools_ms": 0.0, "history_messages": len(history or [])}
        records: list[ToolCallRecord] = []
        contents: list[types.Content] = list(history or [])
        contents.append(types.Content(role="user", parts=[types.Part.from_text(text=message)]))
        config_auto, config_none = self._configs_for_request()
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
                continue

            if not acc["text"]:
                reason = acc["blocked"] or "Réponse vide de Gemini."
                streamed.append(reason)
                yield {"type": "token", "text": reason}
            break

        total = (time.perf_counter() - t0) * 1000
        gemini_ms = sum(c["duration_ms"] for c in metrics["gemini_calls"])
        metrics.update({
            "total_ms": round(total, 1),
            "first_token_ms": round(first_token_ms, 1) if first_token_ms is not None else None,
            "gemini_ms": round(gemini_ms, 1),
            "tools_ms": round(metrics["tools_ms"], 1),
            "overhead_ms": round(total - gemini_ms - metrics["tools_ms"], 1),
            "gemini_call_count": len(metrics["gemini_calls"]),
            "model": self._settings.gemini_model,
        })
        logger.info("Stream terminé en %.0f ms (1er token à %s ms)", total, metrics["first_token_ms"])
        yield {"type": "done", "response": "".join(streamed).strip(),
               "tool_calls": [r.as_dict() for r in records],
               "max_turns_reached": reached_limit, "metrics": metrics}
