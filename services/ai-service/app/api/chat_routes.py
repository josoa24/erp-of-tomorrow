"""Endpoints de discussion : réponse complète (JSON) ou en streaming (SSE)."""
from __future__ import annotations

import logging
import uuid
from typing import AsyncIterator

from fastapi import APIRouter, Request
from fastapi.responses import StreamingResponse

from app.agent.confirmation import ActionGuard
from app.agent.memory import Conversation, ConversationStore
from app.agent.orchestrator import AgentError
from app.api.identity import user_from_request
from app.api.schemas import ChatRequest, ChatResponse, ToolCallOut, api_error, sse
from app.erp.context import UserContext, current_user

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/agent", tags=["agent"])


def open_conversation(request: Request, conv_id: str | None, user: UserContext) -> tuple[Conversation, ActionGuard] | None:
    """Ouvre (ou crée) la conversation de cet utilisateur et démarre un nouveau tour."""
    store: ConversationStore = request.app.state.conversations
    cid = conv_id or uuid.uuid4().hex
    conv, existed = store.get_or_create(cid)
    if not existed:
        conv.owner = user.user_id
    elif conv.owner != user.user_id:
        return None  # conversation d'un autre utilisateur : on ne révèle rien
    conv.turn_counter += 1
    ttl = request.app.state.settings.confirmation_ttl_min * 60
    return conv, ActionGuard(conv.pending, conv.turn_counter, ttl_s=ttl)


@router.post("/chat", response_model=ChatResponse, response_model_exclude_none=True)
async def chat(body: ChatRequest, request: Request):
    user = user_from_request(request)
    agent, store = request.app.state.agent, request.app.state.conversations
    if agent is None:
        return api_error(503, "SERVER", "GEMINI_API_KEY manquante : assistant indisponible.")
    opened = open_conversation(request, body.conversation_id, user)
    if opened is None:
        return api_error(404, "NOT_FOUND", "Conversation inconnue.")
    conv, guard = opened
    token = current_user.set(user)
    try:
        async with conv.lock:
            result = await agent.run(body.message, history=store.history(conv), guard=guard)
            if result.response:
                store.append_exchange(conv, body.message, result.response)
    finally:
        current_user.reset(token)
    return ChatResponse(response=result.response, conversation_id=conv.id, history_turns=conv.turns,
                        tool_calls=[ToolCallOut(**c.as_dict()) for c in result.tool_calls],
                        pending_actions=guard.pending(), max_turns_reached=result.max_turns_reached,
                        metrics=result.metrics)


@router.post("/chat/stream", responses={200: {"content": {"text/event-stream": {}}, "description":
             "Événements SSE : start, token, tool_call, tool_result, done (ou error)"}})
async def chat_stream(body: ChatRequest, request: Request):
    """Événements : `start` {conversation_id} → `token` {text} ×N, `tool_call`, `tool_result`
    → `done` {response, tool_calls, pending_actions, metrics, history_turns} — ou `error` {type, message}."""
    user = user_from_request(request)
    agent, store = request.app.state.agent, request.app.state.conversations
    if agent is None:
        return api_error(503, "SERVER", "GEMINI_API_KEY manquante : assistant indisponible.")
    opened = open_conversation(request, body.conversation_id, user)
    if opened is None:
        return api_error(404, "NOT_FOUND", "Conversation inconnue.")
    conv, guard = opened

    async def events() -> AsyncIterator[str]:
        token = current_user.set(user)  # dans la tâche qui exécute le flux
        try:
            async with conv.lock:
                yield sse("start", {"conversation_id": conv.id})
                async for ev in agent.run_stream(body.message, history=store.history(conv), guard=guard):
                    kind = ev.pop("type")
                    if kind == "done":
                        if ev["response"]:
                            store.append_exchange(conv, body.message, ev["response"])
                        ev.update(conversation_id=conv.id, history_turns=conv.turns, pending_actions=guard.pending())
                    yield sse(kind, ev)
        except AgentError as exc:
            yield sse("error", {"type": "SERVER", "code": exc.code, "message": exc.message})
        except Exception:  # noqa: BLE001
            logger.exception("Erreur pendant le streaming")
            yield sse("error", {"type": "SERVER", "code": "INTERNAL_ERROR", "message": "Erreur interne."})
        finally:
            current_user.reset(token)

    return StreamingResponse(events(), media_type="text/event-stream",
                             headers={"Cache-Control": "no-cache", "X-Accel-Buffering": "no"})
