"""Erreurs d'appel Gemini converties en AgentError, sans aucune donnée sensible."""
from __future__ import annotations

import asyncio
import logging

from google.genai import errors as genai_errors

logger = logging.getLogger(__name__)


class AgentError(Exception):
    """Erreur côté Gemini, sans aucune donnée sensible."""

    def __init__(self, status_code: int, code: str, message: str) -> None:
        super().__init__(message)
        self.status_code = status_code
        self.code = code
        self.message = message


def _is_timeout(exc: BaseException) -> bool:
    while exc is not None:
        if isinstance(exc, (TimeoutError, asyncio.TimeoutError)) or "Timeout" in type(exc).__name__:
            return True
        exc = exc.__cause__ or exc.__context__  # type: ignore[assignment]
    return False


def to_agent_error(exc: Exception) -> AgentError:
    """Convertit toute erreur d'appel Gemini (API, timeout, réseau) en AgentError sans secret."""
    if isinstance(exc, AgentError):
        return exc
    if isinstance(exc, genai_errors.APIError):
        logger.warning("Erreur Gemini %s %s", exc.code, exc.status)
        return _map_api_error(exc)
    if _is_timeout(exc):
        return AgentError(504, "GEMINI_TIMEOUT", "Gemini n'a pas répondu à temps.")
    logger.warning("Erreur réseau Gemini : %s", type(exc).__name__)
    return AgentError(502, "GEMINI_NETWORK_ERROR", f"Erreur réseau vers Gemini ({type(exc).__name__}).")


def _map_api_error(exc: genai_errors.APIError) -> AgentError:
    code = exc.code or 0
    msg = (exc.message or "").strip()[:300]
    if code == 429:
        return AgentError(503, "GEMINI_RATE_LIMIT", "Quota Gemini atteint, réessayez dans un instant.")
    if code == 404:
        return AgentError(502, "GEMINI_MODEL_NOT_FOUND", f"Modèle Gemini introuvable ou indisponible : {msg}")
    if code in (401, 403) or "API key" in msg:
        return AgentError(502, "GEMINI_AUTH_ERROR", "Clé API Gemini invalide ou non autorisée.")
    if 400 <= code < 500:
        return AgentError(502, "GEMINI_BAD_REQUEST", f"Requête refusée par Gemini ({code}) : {msg}")
    return AgentError(502, "GEMINI_SERVER_ERROR", f"Erreur serveur Gemini ({code}).")
