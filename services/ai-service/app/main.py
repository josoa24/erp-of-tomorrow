"""ai-service : assistant IA de l'ERP (port 8086, derrière la gateway sur /api/ai).

Local     :  uvicorn app.main:app --reload --port 8086
Via ERP   :  http://localhost:8080/api/ai/ui   (page de test)  — API : /api/ai/agent/...
"""
from __future__ import annotations

import logging
from contextlib import asynccontextmanager
from typing import Any

import httpx
from fastapi import FastAPI

from app.agent.gemini_client import build_client, check_model
from app.agent.memory import ConversationStore
from app.agent.orchestrator import GeminiAgent
from app.api import audit_routes, chat_routes, conversation_routes, invoice_routes, system_routes
from app.api.error_handlers import register_error_handlers
from app.api.rate_limit import SlidingWindowLimiter
from app.audit.store import AuditStore
from app.config import Settings, get_settings
from app.erp.client import ErpClient
from app.invoices.extractor import InvoiceExtractor
from app.tools.registry import ToolRegistry

logger = logging.getLogger("ai-service")


async def _gemini(settings: Settings, client: Any) -> Any:
    """Client injecté (tests), sinon client réel si la clé est présente (modèle vérifié au démarrage)."""
    if client is not None or not settings.has_api_key:
        return client
    client = build_client(settings)
    if settings.check_model_on_startup:
        status = await check_model(client, settings.gemini_model)
        log = logger.info if status["available"] else logger.error
        log("Modèle %s : %s", settings.gemini_model, "disponible" if status["available"] else status.get("error"))
    return client


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
        app.state.audit = AuditStore(settings.audit_db_path)
        app.state.rate_limiter = SlidingWindowLimiter(settings.ai_rate_limit_per_minute)
        gemini = await _gemini(settings, client)
        app.state.agent = GeminiAgent(gemini, app.state.registry, settings) if gemini is not None else None
        app.state.invoice_extractor = InvoiceExtractor(gemini, settings) if gemini is not None else None
        if gemini is None:
            logger.error("GEMINI_API_KEY absente : l'assistant renverra 503.")
        yield
        app.state.audit.close()
        if erp_http_client is None:
            await erp_http.aclose()

    app = FastAPI(title="AI", version="1.1.0", lifespan=lifespan, openapi_url="/v3/api-docs",
                  description="Assistant IA de l'ERP. Toutes les routes métier exigent le token (via la gateway). "
                              "Les écritures sont proposées puis exécutées après confirmation.")
    register_error_handlers(app)
    for module in (chat_routes, conversation_routes, invoice_routes, audit_routes, system_routes):
        app.include_router(module.router)
    return app


app = create_app()
