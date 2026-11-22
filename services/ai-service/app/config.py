"""Configuration du service IA, chargée depuis le fichier .env.

Chaque champ se règle par la variable d'environnement de même nom en majuscules
(ex. `max_tool_turns` -> MAX_TOOL_TURNS) ; une variable vide garde la valeur par défaut.
La clé API n'est jamais exposée : elle est stockée comme SecretStr et
masquée dans toute représentation (repr, logs, réponses HTTP).
"""
from __future__ import annotations

import os
from functools import lru_cache
from pathlib import Path

from dotenv import load_dotenv
from pydantic import BaseModel, ConfigDict, Field, SecretStr, field_validator

SERVICE_DIR = Path(__file__).resolve().parent.parent
# Charge ai-service/.env quel que soit le dossier depuis lequel on lance uvicorn.
load_dotenv(SERVICE_DIR / ".env")


class Settings(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)
    gemini_api_key: SecretStr = Field(default=SecretStr(""))
    gemini_model: str = "gemini-3.5-flash-lite"

    max_tool_turns: int = Field(default=5, ge=1, le=20)
    tool_timeout_s: float = Field(default=10.0, gt=0)

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
    # Origines autorisées (futur front React), séparées par des virgules dans CORS_ORIGINS
    cors_origins: list[str] = ["http://localhost:5173", "http://localhost:3000"]

    confirmation_ttl_min: float = Field(default=10, gt=0)

    # Mémoire de conversation (RAM, non persistante) et durée de validité d'une saisie en attente
    conversation_ttl_min: float = Field(default=30, gt=0)
    max_history_turns: int = Field(default=6, ge=0, le=50)
    history_max_chars: int = Field(default=1500, ge=100)
    max_conversations: int = Field(default=1000, ge=1)

    # Journal des actions IA (SQLite ; chemin relatif = relatif au dossier ai-service ; ":memory:" possible)
    ai_audit_db: str = "data/ai_audit.db"
    # Limite de débit par utilisateur (requêtes Gemini par minute, fenêtre glissante)
    ai_rate_limit_per_minute: int = Field(default=20, ge=1)
    # Factures fournisseurs : taille maximale du document envoyé à /invoices/extract
    invoice_max_mb: float = Field(default=8, gt=0, le=20)

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

    @field_validator("log_level")
    @classmethod
    def _upper(cls, v: str) -> str:
        return v.upper()

    @property
    def has_api_key(self) -> bool:
        key = self.gemini_api_key.get_secret_value()
        return bool(key) and key != "ma_cle_api"

    @property
    def audit_db_path(self) -> str:
        path = Path(self.ai_audit_db)
        return self.ai_audit_db if self.ai_audit_db == ":memory:" or path.is_absolute() else str(SERVICE_DIR / path)

    @property
    def invoice_max_bytes(self) -> int:
        return int(self.invoice_max_mb * 1024 * 1024)


@lru_cache
def get_settings() -> Settings:
    values: dict[str, object] = {}
    for name in Settings.model_fields:
        raw = os.getenv(name.upper(), "").strip()
        if raw:
            values[name] = [o.strip() for o in raw.split(",") if o.strip()] if name == "cors_origins" else raw
    return Settings(**values)
