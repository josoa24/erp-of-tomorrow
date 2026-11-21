"""Schémas HTTP de l'ai-service et format d'erreur commun à l'ERP."""
from __future__ import annotations

import json
from datetime import datetime, timezone
from typing import Any

from fastapi.responses import JSONResponse
from pydantic import BaseModel, ConfigDict, Field

CONV_ID = r"^[A-Za-z0-9_-]{1,64}$"
ACTION_ID = r"^[a-f0-9]{1,32}$"


class ChatRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True, json_schema_extra={
        "example": {"message": "Quels produits risquent la rupture ?", "conversation_id": None}})
    message: str = Field(min_length=1, max_length=4000, description="Demande de l'utilisateur")
    conversation_id: str | None = Field(default=None, pattern=CONV_ID,
                                        description="Absent = nouvelle conversation ; renvoyer l'id reçu pour continuer.")


class ToolCallOut(BaseModel):
    name: str
    arguments: dict[str, Any]
    success: bool
    duration_ms: float
    turn: int
    error_code: str | None = None
    status: str | None = None          # pending_confirmation | executed | failed | cancelled
    action: dict[str, Any] | None = None


class ChatResponse(BaseModel):
    response: str
    conversation_id: str
    history_turns: int
    tool_calls: list[ToolCallOut] = []
    pending_actions: list[dict[str, Any]] = []
    max_turns_reached: bool = False
    metrics: dict[str, Any] | None = None


def api_error(status: int, type_: str, message: str) -> JSONResponse:
    """Même forme que les autres services : {status, type, message, timestamp}."""
    return JSONResponse(status_code=status, content={
        "status": status, "type": type_, "message": message,
        "timestamp": datetime.now(timezone.utc).isoformat(timespec="seconds").replace("+00:00", "Z")})


def sse(event: str, data: dict[str, Any]) -> str:
    return f"event: {event}\ndata: {json.dumps(data, ensure_ascii=False, default=str)}\n\n"
