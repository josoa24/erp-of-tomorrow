"""Routes système : page de test, santé, outils disponibles pour le rôle connecté."""
from __future__ import annotations

from pathlib import Path
from typing import Any

from fastapi import APIRouter, Request
from fastapi.responses import FileResponse

from app.api.identity import user_from_request

STATIC_DIR = Path(__file__).resolve().parent.parent / "static"
router = APIRouter()


@router.get("/ui", include_in_schema=False)
async def chat_ui() -> FileResponse:
    """Page de test (publique via la gateway : elle ne contient aucune donnée)."""
    return FileResponse(STATIC_DIR / "chat.html")


@router.get("/health", tags=["system"])
async def health() -> dict[str, str]:
    return {"status": "ok", "service": "ai-service"}


@router.get("/tools", tags=["agent"])
async def tools(request: Request) -> dict[str, Any]:
    """Outils disponibles pour le rôle de l'utilisateur connecté."""
    user = user_from_request(request)
    described = request.app.state.registry.describe(user.role)
    return {"role": user.role, "count": len(described), "tools": described}
