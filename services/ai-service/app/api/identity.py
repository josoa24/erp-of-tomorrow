"""Lecture de l'identité transmise par la gateway (JWT déjà vérifié par elle)."""
from __future__ import annotations

from fastapi import Request

from app.erp.context import ROLES, UserContext, normalize_roles


class NotAuthenticated(Exception):
    pass


def user_from_request(request: Request) -> UserContext:
    """L'ai-service n'est joignable que derrière la gateway : elle a validé le token et ajouté X-User-*."""
    auth = request.headers.get("authorization", "")
    role = request.headers.get("x-user-role", "")
    user_id = request.headers.get("x-user-id", "")
    if not auth.startswith("Bearer ") or role not in ROLES or not user_id:
        raise NotAuthenticated()
    roles = normalize_roles(request.headers.get("x-user-roles", "").replace(" ", "").split(",")) or (role,)
    return UserContext(token=auth[7:], user_id=user_id, email=request.headers.get("x-user-email", ""),
                       role=roles[0], roles=roles)
