"""Garde-fou des saisies : aucune écriture sans confirmation explicite de l'utilisateur.

Principe (appliqué par le serveur, pas seulement par le prompt) :
1. Quand Gemini appelle un outil d'écriture (create_sale, add_stock, ...), l'outil
   N'EST PAS exécuté. On crée une « action en attente » et on renvoie à Gemini
   {"status": "CONFIRMATION_REQUIRED", "action_id": ..., "summary": ...}.
   Gemini présente alors un récapitulatif et pose la question.
2. L'action n'est exécutée que :
   - si l'utilisateur clique « Confirmer » (endpoint dédié, aucune décision de l'IA), ou
   - si Gemini appelle confirm_action dans un tour POSTÉRIEUR à la proposition
     (l'IA ne peut donc jamais confirmer elle-même dans le même tour).
3. Les actions expirent (CONFIRMATION_TTL_MIN) et ne peuvent être exécutées qu'une fois.
"""
from __future__ import annotations

import time
import uuid
from dataclasses import dataclass, field
from typing import Any, Callable


@dataclass
class PendingAction:
    id: str
    tool: str
    arguments: dict[str, Any]
    summary: str
    created_turn: int
    created_at: float = field(default_factory=time.monotonic)

    def public(self) -> dict[str, Any]:
        return {"action_id": self.id, "tool": self.tool, "arguments": self.arguments, "summary": self.summary}


class ActionGuard:
    """Vue des actions en attente d'UNE conversation, pour le tour en cours."""

    def __init__(self, pending: dict[str, PendingAction], current_turn: int, ttl_s: float = 600,
                 clock: Callable[[], float] = time.monotonic) -> None:
        self._pending = pending          # dict partagé avec la Conversation (mémoire RAM)
        self.current_turn = current_turn
        self._ttl_s = ttl_s
        self._clock = clock

    def _purge(self) -> None:
        now = self._clock()
        for aid in [a for a, p in self._pending.items() if now - p.created_at > self._ttl_s]:
            del self._pending[aid]

    def pending(self) -> list[dict[str, Any]]:
        self._purge()
        return [p.public() for p in self._pending.values()]

    def propose(self, tool: str, arguments: dict[str, Any], summary: str) -> dict[str, Any]:
        self._purge()
        action = PendingAction(id=uuid.uuid4().hex[:8], tool=tool, arguments=arguments, summary=summary,
                               created_turn=self.current_turn, created_at=self._clock())
        self._pending[action.id] = action
        return {
            "success": True,
            "status": "CONFIRMATION_REQUIRED",
            **action.public(),
            "message": ("Action NON exécutée. Présente ce récapitulatif à l'utilisateur et demande-lui "
                        "de confirmer explicitement (oui / non)."),
        }

    def take(self, action_id: str, *, by_user: bool) -> PendingAction | dict[str, Any]:
        """Retire et renvoie l'action à exécuter, ou un dict d'erreur.

        by_user=True  : clic sur « Confirmer » (décision humaine directe).
        by_user=False : confirm_action appelé par Gemini -> refusé dans le tour de la proposition.
        """
        self._purge()
        action = self._pending.get(action_id)
        if action is None:
            return {"success": False, "error": {
                "code": "ACTION_NOT_FOUND",
                "message": "Aucune action en attente avec cet identifiant (déjà traitée ou expirée).",
                "pending_actions": self.pending()}}
        if not by_user and action.created_turn >= self.current_turn:
            return {"success": False, "error": {
                "code": "CONFIRMATION_NOT_GIVEN",
                "message": ("L'utilisateur n'a pas encore répondu. Demande-lui de confirmer et attends "
                            "sa réponse avant d'appeler confirm_action.")}}
        del self._pending[action_id]
        return action

    def cancel(self, action_id: str) -> dict[str, Any]:
        self._purge()
        action = self._pending.pop(action_id, None)
        if action is None:
            return {"success": False, "error": {"code": "ACTION_NOT_FOUND",
                                                "message": "Aucune action en attente avec cet identifiant."}}
        return {"success": True, "status": "CANCELLED", **action.public(),
                "message": "Action annulée, rien n'a été enregistré."}
