"""ai-service : assistant IA de l'ERP (port 8086, derrière la gateway sur /api/ai).

Local     :  uvicorn app.main:app --reload --port 8086
Via ERP   :  http://localhost:8080/api/ai/ui   (page de test)  — API : /api/ai/agent/...
"""
from __future__ import annotations

import logging
from contextlib import asynccontextmanager
from pathlib import Path as FsPath
from typing import Any

import httpx
from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import FileResponse

from app.agent.memory import ConversationStore
from app.agent.orchestrator import AgentError, GeminiAgent, build_client, check_model
from app.api import chat_routes, conversation_routes
from app.api.identity import NotAuthenticated, user_from_request
from app.api.schemas import api_error
from app.config import Settings, get_settings
from app.erp.client import ErpClient
from app.tools.registry import ToolRegistry

logger = logging.getLogger("ai-service")
STATIC_DIR = FsPath(__file__).resolve().parent / "static"


def create_app(settings: Settings | None = None, client: Any = None,
               erp_http_client: httpx.AsyncClient | None = None) -> FastAPI:
    """`client` (Gemini) et `erp_http_client` (gateway) sont injectables pour les tests."""
    settings = settings or get_settings()
    logging.basicConfig(level=settings.log_level, format="%(asctime)s %(levelname)-7s %(name)s - %(message)s")

    @asynccontextmanager
    async def lifespan(app: FastAPI):
        erp_http = erp_http_client or ErpClient.create_http(settings.erp_gateway_url, settings.erp_timeout_s)
        app.state.settings = settings
        app.state.erp_http = erp_http
        app.state.registry = ToolRegistry(ErpClient(erp_http), timeout_s=settings.tool_timeout_s)
        app.state.conversations = ConversationStore(
            ttl_s=settings.conversation_ttl_min * 60, max_turns=settings.max_history_turns,
            max_chars=settings.history_max_chars, max_conversations=settings.max_conversations)
        gemini = client
        if gemini is None and settings.has_api_key:
            gemini = build_client(settings)
            if settings.check_model_on_startup:
                status = await check_model(gemini, settings.gemini_model)
                log = logger.info if status["available"] else logger.error
                log("Modèle %s : %s", settings.gemini_model, "disponible" if status["available"] else status.get("error"))
        app.state.agent = GeminiAgent(gemini, app.state.registry, settings) if gemini is not None else None
        if gemini is None:
            logger.error("GEMINI_API_KEY absente : l'assistant renverra 503.")
        yield
        if erp_http_client is None:
            await erp_http.aclose()

    app = FastAPI(title="AI", version="1.0.0", lifespan=lifespan, openapi_url="/v3/api-docs",
                  description="Assistant IA de l'ERP. Toutes les routes métier exigent le token (via la gateway). "
                              "Les écritures sont proposées puis exécutées après confirmation.")

    @app.exception_handler(NotAuthenticated)
    async def _unauth(_: Request, __: NotAuthenticated):
        return api_error(401, "UNAUTHORIZED", "Authentification requise (passer par la gateway avec un token).")

    @app.exception_handler(RequestValidationError)
    async def _invalid(_: Request, exc: RequestValidationError):
        first = exc.errors()[0] if exc.errors() else {}
        return api_error(400, "VALIDATION", f"Requête invalide : {'.'.join(map(str, first.get('loc', [])))} "
                                             f"{first.get('msg', '')}".strip())

    @app.exception_handler(AgentError)
    async def _agent(_: Request, exc: AgentError):
        return api_error(exc.status_code, "SERVER", exc.message)

    @app.exception_handler(Exception)
    async def _unexpected(_: Request, exc: Exception):
        logger.exception("Erreur inattendue")
        return api_error(500, "SERVER", "Erreur interne de l'assistant.")

    app.include_router(chat_routes.router)
    app.include_router(conversation_routes.router)

    @app.get("/ui", include_in_schema=False)
    async def chat_ui() -> FileResponse:
        """Page de test (publique via la gateway : elle ne contient aucune donnée)."""
        return FileResponse(STATIC_DIR / "chat.html")

    @app.get("/health", tags=["system"])
    async def health() -> dict[str, str]:
        return {"status": "ok", "service": "ai-service"}

    @app.get("/tools", tags=["agent"])
    async def tools(request: Request) -> dict[str, Any]:
        """Outils disponibles pour le rôle de l'utilisateur connecté."""
        user = user_from_request(request)
        described = request.app.state.registry.describe(user.role)
        return {"role": user.role, "count": len(described), "tools": described}

    return app


app = create_app()
