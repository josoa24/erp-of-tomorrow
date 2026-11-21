"""Configuration du service IA, chargée depuis le fichier .env.

La clé API n'est jamais exposée : elle est stockée comme SecretStr et
masquée dans toute représentation (repr, logs, réponses HTTP).
"""
from __future__ import annotations

import os
from functools import lru_cache
from pathlib import Path

from dotenv import load_dotenv
from pydantic import BaseModel, Field, SecretStr, field_validator

# Charge ai-service/.env quel que soit le dossier depuis lequel on lance uvicorn.
_ENV_FILE = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(_ENV_FILE)


def _opt_int(name: str) -> int | None:
    value = os.getenv(name, "").strip()
    return int(value) if value else None


class Settings(BaseModel):
    gemini_api_key: SecretStr = Field(default=SecretStr(""))
    gemini_model: str = "gemini-3.5-flash-lite"

    # Orchestration
    max_tool_turns: int = Field(default=5, ge=1, le=20)
    tool_timeout_s: float = Field(default=10.0, gt=0)

    # Appels Gemini
    gemini_timeout_s: float = Field(default=30.0, gt=0)
    gemini_retry_attempts: int = Field(default=2, ge=1, le=5)
    gemini_temperature: float | None = None
    # Gemini 2.5 : budget de réflexion (0 = désactivé, le plus rapide sur 2.5 Flash)
    gemini_thinking_budget: int | None = None
    # Gemini 3.x : niveau de réflexion (minimal, low, medium, high)
    gemini_thinking_level: str | None = None

    # Gateway de l'ERP (tous les appels métier passent par elle, avec le token de l'utilisateur)
    erp_gateway_url: str = "http://localhost:8080"
    erp_timeout_s: float = Field(default=10.0, gt=0)
    # Origines autorisées (futur front React)
    cors_origins: list[str] = ["http://localhost:5173", "http://localhost:3000"]

    # Saisies : durée de validité d'une action en attente de confirmation
    confirmation_ttl_min: float = Field(default=10, gt=0)

    # Mémoire de conversation (RAM, non persistante)
    conversation_ttl_min: float = Field(default=30, gt=0)
    max_history_turns: int = Field(default=6, ge=0, le=50)
    history_max_chars: int = Field(default=1500, ge=100)
    max_conversations: int = Field(default=1000, ge=1)

    # Vérifie au démarrage que le modèle existe (1 appel models.get)
    check_model_on_startup: bool = True
    log_level: str = "INFO"

    @field_validator("gemini_thinking_level")
    @classmethod
    def _check_level(cls, v: str | None) -> str | None:
        if v is None:
            return None
        v = v.strip().lower()
        if v and v not in {"minimal", "low", "medium", "high"}:
            raise ValueError("GEMINI_THINKING_LEVEL doit valoir minimal, low, medium ou high")
        return v or None

    @property
    def has_api_key(self) -> bool:
        key = self.gemini_api_key.get_secret_value()
        return bool(key) and key != "ma_cle_api"


@lru_cache
def get_settings() -> Settings:
    temp = os.getenv("GEMINI_TEMPERATURE", "").strip()
    return Settings(
        gemini_api_key=SecretStr(os.getenv("GEMINI_API_KEY", "")),
        gemini_model=os.getenv("GEMINI_MODEL", "gemini-3.5-flash-lite").strip(),
        max_tool_turns=int(os.getenv("MAX_TOOL_TURNS", "5")),
        tool_timeout_s=float(os.getenv("TOOL_TIMEOUT_S", "10")),
        gemini_timeout_s=float(os.getenv("GEMINI_TIMEOUT_S", "30")),
        gemini_retry_attempts=int(os.getenv("GEMINI_RETRY_ATTEMPTS", "2")),
        gemini_temperature=float(temp) if temp else None,
        gemini_thinking_budget=_opt_int("GEMINI_THINKING_BUDGET"),
        gemini_thinking_level=os.getenv("GEMINI_THINKING_LEVEL") or None,
        erp_gateway_url=os.getenv("ERP_GATEWAY_URL", "http://localhost:8080").strip(),
        erp_timeout_s=float(os.getenv("ERP_TIMEOUT_S", "10")),
        cors_origins=[o.strip() for o in os.getenv(
            "CORS_ORIGINS", "http://localhost:5173,http://localhost:3000").split(",") if o.strip()],
        confirmation_ttl_min=float(os.getenv("CONFIRMATION_TTL_MIN", "10")),
        conversation_ttl_min=float(os.getenv("CONVERSATION_TTL_MIN", "30")),
        max_history_turns=int(os.getenv("MAX_HISTORY_TURNS", "6")),
        history_max_chars=int(os.getenv("HISTORY_MAX_CHARS", "1500")),
        max_conversations=int(os.getenv("MAX_CONVERSATIONS", "1000")),
        check_model_on_startup=os.getenv("CHECK_MODEL_ON_STARTUP", "true").lower() in {"1", "true", "yes"},
        log_level=os.getenv("LOG_LEVEL", "INFO").upper(),
    )
