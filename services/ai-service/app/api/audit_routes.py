"""Consultation du journal des actions IA, réservée au rôle ADMIN."""
from __future__ import annotations

from fastapi import APIRouter, Query, Request

from app.api.identity import user_from_request
from app.api.schemas import api_error

router = APIRouter(tags=["audit"])


@router.get("/audit")
async def list_audit(request: Request, limit: int = Query(default=50, ge=1, le=500),
                     userId: str | None = Query(default=None, max_length=64)):  # noqa: N803 (nom ERP)
    """Interactions récentes (plus récentes d'abord), filtrables par utilisateur."""
    user = user_from_request(request)
    if "ADMIN" not in user.roles:
        return api_error(403, "FORBIDDEN", "Le journal des actions IA est réservé au rôle ADMIN.")
    items = request.app.state.audit.list(limit=limit, user_id=userId)
    return {"count": len(items), "items": items}
