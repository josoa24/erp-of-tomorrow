"""Conversations : historique, saisies en attente, boutons Confirmer / Annuler."""
from __future__ import annotations

from fastapi import APIRouter, Path, Request

from app.agent.confirmation import ActionGuard
from app.agent.memory import Conversation
from app.api.identity import user_from_request
from app.api.schemas import ACTION_ID, CONV_ID, api_error
from app.api.turn_support import record_interaction
from app.erp.context import current_user

router = APIRouter(prefix="/agent/conversations", tags=["conversations"])


def _own(request: Request, conversation_id: str) -> Conversation | None:
    user = user_from_request(request)
    conv = request.app.state.conversations.get(conversation_id)
    return conv if conv is not None and conv.owner == user.user_id else None


def _guard(request: Request, conv: Conversation) -> ActionGuard:
    return ActionGuard(conv.pending, conv.turn_counter, ttl_s=request.app.state.settings.confirmation_ttl_min * 60)


@router.get("/{conversation_id}")
async def get_conversation(request: Request, conversation_id: str = Path(pattern=CONV_ID)):
    conv = _own(request, conversation_id)
    if conv is None:
        return api_error(404, "NOT_FOUND", "Conversation inconnue ou expirée.")
    return {"conversation_id": conv.id, "turns": conv.turns, "pending_actions": _guard(request, conv).pending(),
            "messages": [{"role": m.role, "text": "".join(p.text or "" for p in m.parts)} for m in conv.messages]}


@router.delete("/{conversation_id}")
async def delete_conversation(request: Request, conversation_id: str = Path(pattern=CONV_ID)):
    conv = _own(request, conversation_id)
    deleted = conv is not None and request.app.state.conversations.delete(conversation_id)
    return {"conversation_id": conversation_id, "deleted": deleted}


@router.post("/{conversation_id}/actions/{action_id}/confirm")
async def confirm(request: Request, conversation_id: str = Path(pattern=CONV_ID),
                  action_id: str = Path(pattern=ACTION_ID)):
    """Confirmation explicite (bouton) : exécute l'écriture avec le token de l'utilisateur, sans passer par l'IA."""
    conv = _own(request, conversation_id)
    if conv is None:
        return api_error(404, "NOT_FOUND", "Conversation inconnue ou expirée.")
    user = user_from_request(request)
    token = current_user.set(user)
    try:
        async with conv.lock:
            action = _guard(request, conv).take(action_id, by_user=True)
            if isinstance(action, dict):
                return api_error(404, "NOT_FOUND", action["error"]["message"])
            result = await request.app.state.registry.execute_pending(action, user.role)
            ok = result.get("success", False)
            detail = "" if ok else (result.get("error") or {}).get("message", "")
            request.app.state.conversations.append_exchange(
                conv, f"[Confirmé via le bouton] {action.summary}",
                "Écriture effectuée." if ok else f"Écriture refusée par l'ERP : {detail}")
            record_interaction(request, user, conv.id, "[Bouton Confirmer]", [{"name": action.tool, "status":
                               "executed" if ok else "failed", "action": action.public()}], None)
    finally:
        current_user.reset(token)
    if not ok:
        err = result.get("error") or {}
        return api_error(err.get("status") or 422, err.get("code", "BUSINESS"), detail)
    return result


@router.post("/{conversation_id}/actions/{action_id}/cancel")
async def cancel(request: Request, conversation_id: str = Path(pattern=CONV_ID),
                 action_id: str = Path(pattern=ACTION_ID)):
    conv = _own(request, conversation_id)
    if conv is None:
        return api_error(404, "NOT_FOUND", "Conversation inconnue ou expirée.")
    async with conv.lock:
        result = _guard(request, conv).cancel(action_id)
        if not result.get("success"):
            return api_error(404, "NOT_FOUND", result["error"]["message"])
        request.app.state.conversations.append_exchange(
            conv, f"[Annulé via le bouton] {result['summary']}", "Écriture annulée, rien n'a été enregistré.")
        record_interaction(request, user_from_request(request), conv.id, "[Bouton Annuler]",
                           [{"name": result["tool"], "status": "cancelled", "action": result}], None)
    return result
