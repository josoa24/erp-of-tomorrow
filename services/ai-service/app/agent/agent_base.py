"""Socle commun des deux modes de l'agent (JSON et streaming) : outils, métriques, confiance."""
from __future__ import annotations

import asyncio
import logging
import time
from typing import Any

from google import genai
from google.genai import types

from app.agent.confirmation import ActionGuard
from app.agent.gemini_config import AgentConfigs
from app.agent.records import ToolCallRecord, make_record
from app.config import Settings
from app.tools.registry import ToolRegistry

logger = logging.getLogger(__name__)


class AgentBase:
    def __init__(self, client: genai.Client, registry: ToolRegistry, settings: Settings) -> None:
        self._client = client
        self._registry = registry
        self._settings = settings
        self._configs = AgentConfigs(registry, settings)

    @staticmethod
    def _start(message: str, history: list[types.Content] | None) -> tuple[list[types.Content], dict[str, Any]]:
        contents: list[types.Content] = list(history or [])
        contents.append(types.Content(role="user", parts=[types.Part.from_text(text=message)]))
        return contents, {"gemini_calls": [], "tools_ms": 0.0, "history_messages": len(history or [])}

    @staticmethod
    def _usage(turn: int, elapsed: float, usage: Any, **extra: Any) -> dict[str, Any]:
        return {"turn": turn, "duration_ms": round(elapsed, 1), **extra,
                "prompt_tokens": getattr(usage, "prompt_token_count", None),
                "output_tokens": getattr(usage, "candidates_token_count", None),
                "thinking_tokens": getattr(usage, "thoughts_token_count", None)}

    def _finish_metrics(self, metrics: dict[str, Any], t0: float, **extra: Any) -> float:
        total = (time.perf_counter() - t0) * 1000
        gemini_ms = sum(c["duration_ms"] for c in metrics["gemini_calls"])
        metrics.update({
            "total_ms": round(total, 1), **extra,
            "gemini_ms": round(gemini_ms, 1),
            "tools_ms": round(metrics["tools_ms"], 1),
            "overhead_ms": round(total - gemini_ms - metrics["tools_ms"], 1),
            "gemini_call_count": len(metrics["gemini_calls"]),
            "model": self._settings.gemini_model,
        })
        return total

    async def _run_tools(self, calls: list[types.FunctionCall], turn: int, records: list[ToolCallRecord],
                         metrics: dict[str, Any], guard: ActionGuard | None = None) -> list[types.Part]:
        """Exécute tous les appels d'un même tour en parallèle (ils sont indépendants)."""
        start = time.perf_counter()
        outcomes = await asyncio.gather(*(self._registry.execute_timed(c.name, c.args, guard) for c in calls))
        metrics["tools_ms"] += (time.perf_counter() - start) * 1000

        parts: list[types.Part] = []
        for call, (result, duration) in zip(calls, outcomes):
            records.append(make_record(call, result, duration, turn))
            # Un FunctionResponse par FunctionCall, même nom et même id, dans le même ordre.
            parts.append(types.Part(function_response=types.FunctionResponse(
                id=call.id, name=call.name, response=result)))
        return parts
