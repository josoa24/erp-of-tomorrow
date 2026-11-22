"""Ouverture d'un tour de conversation et journalisation de l'interaction."""
from __future__ import annotations

import logging
import sqlite3
import uuid
from typing import Any, Iterable

from fastapi import Request

from app.agent.confirmation import ActionGuard
from app.agent.memory import Conversation, ConversationStore
from app.audit.entry import build_entry
from app.erp.context import UserContext

logger = logging.getLogger(__name__)


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


def record_interaction(request: Request, user: UserContext, conversation_id: str | None, question: str,
                       tool_calls: Iterable[dict[str, Any]], trust_label: str | None) -> None:
    """Écrit une ligne dans le journal ; une panne du journal ne casse jamais la réponse."""
    try:
        request.app.state.audit.record(build_entry(user, conversation_id, question, tool_calls, trust_label))
    except sqlite3.Error as exc:
        logger.warning("Journal IA indisponible : %s", type(exc).__name__)
