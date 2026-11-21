"""Traces d'exécution d'un tour : appels d'outils et résultat final de l'agent."""
from __future__ import annotations

from dataclasses import dataclass, field
from typing import Any

from google.genai import types


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
    # Résultat brut : sert seulement à vérifier les chiffres, jamais renvoyé ni journalisé.
    result: dict[str, Any] = field(default_factory=dict, repr=False)

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
    trust: dict[str, Any] = field(default_factory=dict)


def make_record(call: types.FunctionCall, result: dict[str, Any], duration: float, turn: int) -> ToolCallRecord:
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
        error_code=None if ok else result.get("error", {}).get("code"), status=status, action=action,
        result=result)
