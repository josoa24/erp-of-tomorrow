"""Identité de l'utilisateur pour la requête en cours.

La gateway vérifie le JWT puis transmet à l'ai-service :
- `Authorization: Bearer <token>` (réutilisé tel quel pour appeler les autres modules),
- `X-User-Id`, `X-User-Email`, `X-User-Role`.
Le token est stocké dans une ContextVar : chaque requête (et ses tâches asyncio) voit le sien.
"""
from __future__ import annotations

from contextvars import ContextVar
from dataclasses import dataclass

ROLES = ("ADMIN", "STOCK", "VENTE", "RH", "CRM")


@dataclass(frozen=True)
class UserContext:
    token: str
    user_id: str
    email: str
    role: str


current_user: ContextVar[UserContext | None] = ContextVar("current_user", default=None)
