"""Endpoints de discussion : réponse complète (JSON) ou en streaming (SSE)."""
from __future__ import annotations

import logging
from typing import AsyncIterator

from fastapi import APIRouter, Depends, Request
from fastapi.responses import StreamingResponse

from app.agent.gemini_errors import AgentError
from app.api.identity import user_from_request
from app.api.rate_limit import enforce_rate_limit
from app.api.schemas import ChatRequest, ChatResponse, ToolCallOut, api_error, sse
from app.api.turn_support import open_conversation, record_interaction
from app.erp.context import current_user

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/agent", tags=["agent"], dependencies=[Depends(enforce_rate_limit)])


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
    calls = [c.as_dict() for c in result.tool_calls]
    record_interaction(request, user, conv.id, body.message, calls, result.trust.get("label"))
    return ChatResponse(response=result.response, conversation_id=conv.id, history_turns=conv.turns,
                        tool_calls=[ToolCallOut(**c) for c in calls], pending_actions=guard.pending(),
                        max_turns_reached=result.max_turns_reached, metrics=result.metrics, trust=result.trust)


@router.post("/chat/stream", responses={200: {"content": {"text/event-stream": {}}, "description":
             "Événements SSE : start, token, tool_call, tool_result, done (ou error)"}})
async def chat_stream(body: ChatRequest, request: Request):
    """Événements : `start` {conversation_id} → `token` {text} ×N, `tool_call`, `tool_result`
    → `done` {response, tool_calls, pending_actions, metrics, history_turns, trust} — ou `error`."""
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
                        record_interaction(request, user, conv.id, body.message, ev["tool_calls"],
                                           ev["trust"].get("label"))
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
