"""Construction d'une ligne de journal à partir d'un tour (sans aucune donnée sensible)."""
from __future__ import annotations

from typing import Any, Iterable

from app.erp.context import UserContext

QUESTION_MAX = 300
# status d'un appel d'outil -> catégorie d'action journalisée
_ACTION_KIND = {"pending_confirmation": "proposed", "executed": "confirmed", "cancelled": "refused",
                "failed": "failed"}


def empty_actions() -> dict[str, list[dict[str, str]]]:
    return {kind: [] for kind in _ACTION_KIND.values()}


def action_ref(action: dict[str, Any]) -> dict[str, str]:
    """Outil et identifiant seulement : jamais les arguments (compte bancaire, montants...)."""
    return {"tool": str(action.get("tool", "")), "action_id": str(action.get("action_id", ""))}


def build_entry(user: UserContext, conversation_id: str | None, question: str,
                tool_calls: Iterable[dict[str, Any]], trust_label: str | None) -> dict[str, Any]:
    """`tool_calls` : appels au format public (ToolCallRecord.as_dict)."""
    calls = list(tool_calls)
    actions = empty_actions()
    for call in calls:
        kind = _ACTION_KIND.get(call.get("status") or "")
        if kind and call.get("action"):
            actions[kind].append(action_ref(call["action"]))
    return {"user_id": user.user_id, "email": user.email, "role": user.role,
            "conversation_id": conversation_id, "question": question[:QUESTION_MAX],
            "tools": [c["name"] for c in calls], "actions": actions, "trust_label": trust_label}
