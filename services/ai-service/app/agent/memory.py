"""Mémoire de conversation en RAM (non persistante).

Stratégie « historique compact » pour ne pas dégrader la latence :
- on ne garde QUE les messages utilisateur et les réponses finales de l'IA
  (pas les function_call / function_response ni leurs gros JSON) ;
- on limite le nombre d'échanges conservés (MAX_HISTORY_TURNS) ;
- on tronque les réponses longues (HISTORY_MAX_CHARS).
Gemini rappelle simplement un outil si une donnée précise est redemandée,
ce qui garantit aussi des chiffres toujours à jour.

Les conversations expirent après CONVERSATION_TTL_MIN minutes d'inactivité.
Tout est perdu au redémarrage du service (pas de base de données).
"""
from __future__ import annotations

import asyncio
import time
from collections import OrderedDict
from dataclasses import dataclass, field
from typing import Callable

from google.genai import types

from app.agent.confirmation import PendingAction


@dataclass
class Conversation:
    id: str
    messages: list[types.Content] = field(default_factory=list)
    created_at: float = field(default_factory=time.time)
    last_access: float = field(default_factory=time.time)
    lock: asyncio.Lock = field(default_factory=asyncio.Lock)
    # Compteur de messages (non tronqué, contrairement à l'historique) et saisies en attente.
    turn_counter: int = 0
    owner: str | None = None  # X-User-Id : une conversation n'est visible que par son auteur
    pending: dict[str, PendingAction] = field(default_factory=dict)

    @property
    def turns(self) -> int:
        return sum(1 for m in self.messages if m.role == "user")


class ConversationStore:
    def __init__(self, ttl_s: float = 1800, max_turns: int = 6, max_chars: int = 1500,
                 max_conversations: int = 1000, clock: Callable[[], float] = time.monotonic) -> None:
        self.ttl_s = ttl_s
        self.max_turns = max_turns
        self.max_chars = max_chars
        self.max_conversations = max_conversations
        self._clock = clock
        self._items: OrderedDict[str, Conversation] = OrderedDict()

    def _purge(self) -> None:
        now = self._clock()
        for cid in [c for c, conv in self._items.items() if now - conv.last_access > self.ttl_s]:
            del self._items[cid]
        while len(self._items) > self.max_conversations:  # évite une croissance mémoire illimitée
            self._items.popitem(last=False)

    def get_or_create(self, conversation_id: str) -> tuple[Conversation, bool]:
        """Retourne (conversation, existait_deja). Un id expiré redémarre à vide."""
        self._purge()
        conv = self._items.get(conversation_id)
        existed = conv is not None
        if conv is None:
            conv = Conversation(id=conversation_id, last_access=self._clock())
            self._items[conversation_id] = conv
        conv.last_access = self._clock()
        self._items.move_to_end(conversation_id)
        return conv, existed

    def get(self, conversation_id: str) -> Conversation | None:
        self._purge()
        return self._items.get(conversation_id)

    def delete(self, conversation_id: str) -> bool:
        return self._items.pop(conversation_id, None) is not None

    def history(self, conv: Conversation) -> list[types.Content]:
        """Copie de l'historique à préfixer à la prochaine requête Gemini."""
        return list(conv.messages)

    def append_exchange(self, conv: Conversation, user_text: str, model_text: str) -> None:
        if len(model_text) > self.max_chars:
            model_text = model_text[: self.max_chars] + " […]"
        conv.messages.append(types.Content(role="user", parts=[types.Part.from_text(text=user_text)]))
        conv.messages.append(types.Content(role="model", parts=[types.Part.from_text(text=model_text)]))
        excess = len(conv.messages) - 2 * self.max_turns
        if excess > 0:
            del conv.messages[:excess]  # garde les N derniers échanges (paires user/model)
        conv.last_access = self._clock()

    def __len__(self) -> int:
        self._purge()
        return len(self._items)
