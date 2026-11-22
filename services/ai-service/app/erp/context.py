"""Identité de l'utilisateur pour la requête en cours.

La gateway vérifie le JWT puis transmet à l'ai-service :
- `Authorization: Bearer <token>` (réutilisé tel quel pour appeler les autres modules),
- `X-User-Id`, `X-User-Email`, `X-User-Role` (rôle principal), `X-User-Roles` (liste séparée par des virgules).
Le token est stocké dans une ContextVar : chaque requête (et ses tâches asyncio) voit le sien.
"""
from __future__ import annotations

from contextvars import ContextVar
from dataclasses import dataclass
from typing import Iterable

ROLES = ("ADMIN", "STOCK", "VENTE", "RH", "CRM")


def normalize_roles(roles: str | Iterable[str] | None) -> tuple[str, ...]:
    if roles is None:
        return ()
    items = [roles] if isinstance(roles, str) else list(roles)
    return tuple(r for r in ROLES if r in items)


def roles_label(roles: str | Iterable[str] | None) -> str:
    return " + ".join(normalize_roles(roles)) or "inconnu"


@dataclass(frozen=True)
class UserContext:
    token: str
    user_id: str
    email: str
    role: str
    roles: tuple[str, ...] = ()

    def __post_init__(self) -> None:
        object.__setattr__(self, "roles", normalize_roles(self.roles) or normalize_roles(self.role))


current_user: ContextVar[UserContext | None] = ContextVar("current_user", default=None)
