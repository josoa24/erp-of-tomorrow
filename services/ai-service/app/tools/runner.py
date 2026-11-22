"""Exécution protégée d'un outil : timeout, erreurs converties en dict, jamais d'exception."""
from __future__ import annotations

import asyncio
import logging
from typing import Any

from pydantic import BaseModel, ValidationError

from app.erp.client import ErpClient
from app.tools.spec import ToolSpec

logger = logging.getLogger(__name__)


def tool_error(code: str, message: str, **extra: Any) -> dict[str, Any]:
    return {"success": False, "error": {"code": code, "message": message, **extra}}


def invalid_args(exc: ValidationError) -> dict[str, Any]:
    return tool_error("VALIDATION", "Arguments invalides.",
                      details=[{"field": ".".join(map(str, e["loc"])), "issue": e["msg"]} for e in exc.errors()])


async def run_spec(erp: ErpClient | None, spec: ToolSpec, args: BaseModel, timeout_s: float) -> dict[str, Any]:
    if erp is None:
        return tool_error("SERVER", "Client ERP non configuré.")
    try:
        return await asyncio.wait_for(spec.handler(erp, args), timeout=timeout_s)
    except asyncio.TimeoutError:
        return tool_error("SERVER", f"L'outil {spec.name} n'a pas répondu à temps.")
    except Exception:  # noqa: BLE001 - un outil défaillant ne doit pas casser l'agent
        logger.exception("Erreur pendant l'exécution de l'outil %s", spec.name)
        return tool_error("SERVER", f"Erreur interne de l'outil {spec.name}.")
