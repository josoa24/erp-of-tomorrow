"""Création du client Gemini (timeout, nouvelles tentatives) et vérification du modèle."""
from __future__ import annotations

from typing import Any

from google import genai
from google.genai import errors as genai_errors
from google.genai import types

from app.config import Settings

RETRYABLE_STATUS = [429, 500, 502, 503, 504]


def build_client(settings: Settings) -> genai.Client:
    return genai.Client(
        api_key=settings.gemini_api_key.get_secret_value(),
        http_options=types.HttpOptions(
            timeout=int(settings.gemini_timeout_s * 1000),  # en millisecondes
            retry_options=types.HttpRetryOptions(
                attempts=settings.gemini_retry_attempts,
                initial_delay=0.5, max_delay=4.0,
                http_status_codes=RETRYABLE_STATUS,
            ),
        ),
    )


async def check_model(client: genai.Client, model: str) -> dict[str, Any]:
    """Vérifie que le modèle existe et accepte generateContent (donc le function calling)."""
    try:
        info = await client.aio.models.get(model=model)
    except genai_errors.APIError as exc:
        return {"available": False, "model": model, "error": f"{exc.code} {exc.status}: {exc.message}"}
    except Exception as exc:  # réseau, timeout...
        return {"available": False, "model": model, "error": type(exc).__name__}
    actions = list(info.supported_actions or [])
    return {
        "available": True,
        "model": model,
        "display_name": info.display_name,
        "supports_generate_content": (not actions) or "generateContent" in actions,
        "input_token_limit": info.input_token_limit,
    }
