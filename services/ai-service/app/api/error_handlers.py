"""Gestionnaires d'erreurs : toutes les erreurs au format ERP {status, type, message, timestamp}."""
from __future__ import annotations

import logging

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError

from app.agent.gemini_errors import AgentError
from app.api.identity import NotAuthenticated
from app.api.rate_limit import RateLimited
from app.api.schemas import api_error

logger = logging.getLogger("ai-service")


def register_error_handlers(app: FastAPI) -> None:
    @app.exception_handler(NotAuthenticated)
    async def _unauth(_: Request, __: NotAuthenticated):
        return api_error(401, "UNAUTHORIZED", "Authentification requise (passer par la gateway avec un token).")

    @app.exception_handler(RateLimited)
    async def _limited(_: Request, exc: RateLimited):
        resp = api_error(429, "RATE_LIMIT", "Trop de requêtes vers l'assistant : réessayez dans "
                                            f"{exc.retry_after_s} s.")
        resp.headers["Retry-After"] = str(exc.retry_after_s)
        return resp

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
