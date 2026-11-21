"""Registre des outils : liste blanche, filtrage par rôle, validation, confirmation des écritures.

- Gemini ne reçoit QUE les outils des modules autorisés pour le rôle de l'utilisateur
  (moins d'outils = réponses plus rapides, et pas de tentative inutile).
- La gateway de l'ERP revérifie de toute façon chaque appel avec le token (défense en profondeur).
- Aucun outil d'écriture n'est exécuté sans confirmation (voir app/agent/confirmation.py).
"""
from __future__ import annotations

import asyncio
import logging
import time
from typing import Any

from google.genai import types
from pydantic import BaseModel, Field, ValidationError

from app.agent.confirmation import ActionGuard, PendingAction
from app.erp.client import ErpClient
from app.erp.context import current_user
from app.tools import rh_crm_tools, stock_tools, vente_tools
from app.tools.spec import Args, ToolSpec, decl, string

logger = logging.getLogger(__name__)

# Même règle que la gateway (RoleAccessPolicy) : un rôle = un module, ADMIN = tout.
MODULE_ROLES: dict[str, set[str]] = {"stock": {"STOCK"}, "vente": {"VENTE"}, "rh": {"RH"}, "crm": {"CRM"}}


class ActionIdArgs(Args):
    action_id: str = Field(min_length=1, max_length=32)


_ACTION = {"action_id": string("Identifiant reçu avec CONFIRMATION_REQUIRED.")}
SYSTEM_SPECS = [
    ToolSpec(decl("confirm_action", "Exécute une écriture en attente. UNIQUEMENT si le dernier message de "
                  "l'utilisateur est un accord explicite.", _ACTION, ["action_id"]), ActionIdArgs, None, "system"),
    ToolSpec(decl("cancel_action", "Annule une écriture en attente (refus ou modification demandée).",
                  _ACTION, ["action_id"]), ActionIdArgs, None, "system"),
]
ALL_SPECS: list[ToolSpec] = stock_tools.SPECS + vente_tools.SPECS + rh_crm_tools.SPECS + SYSTEM_SPECS


def _err(code: str, message: str, **extra: Any) -> dict[str, Any]:
    return {"success": False, "error": {"code": code, "message": message, **extra}}


class ToolRegistry:
    def __init__(self, erp: ErpClient | None, timeout_s: float = 10.0) -> None:
        self._erp = erp
        self._timeout_s = timeout_s
        self._specs: dict[str, ToolSpec] = {s.name: s for s in ALL_SPECS}
        self._tool_cache: dict[str, types.Tool] = {}

    # ----------------------------- Rôles ----------------------------------- #
    @staticmethod
    def allowed(spec: ToolSpec, role: str | None) -> bool:
        if spec.module == "system" or role == "ADMIN":
            return True
        return role in MODULE_ROLES.get(spec.module, set())

    def specs_for(self, role: str | None) -> list[ToolSpec]:
        return [s for s in self._specs.values() if self.allowed(s, role)]

    def gemini_tool(self, role: str | None) -> types.Tool:
        """Déclarations envoyées à Gemini pour ce rôle (construites une fois puis mises en cache)."""
        key = role or "-"
        if key not in self._tool_cache:
            self._tool_cache[key] = types.Tool(function_declarations=[s.declaration for s in self.specs_for(role)])
        return self._tool_cache[key]

    @property
    def names(self) -> list[str]:
        return list(self._specs)

    def describe(self, role: str | None = None) -> list[dict[str, Any]]:
        specs = self.specs_for(role) if role else list(self._specs.values())
        return [{"name": s.name, "module": s.module, "requires_confirmation": s.requires_confirmation,
                 "description": s.declaration.description,
                 "parameters": s.declaration.parameters.model_dump(mode="json", exclude_none=True)
                 if s.declaration.parameters else None} for s in specs]

    # --------------------------- Exécution --------------------------------- #
    @staticmethod
    def _invalid(exc: ValidationError) -> dict[str, Any]:
        return _err("VALIDATION", "Arguments invalides.",
                    details=[{"field": ".".join(map(str, e["loc"])), "issue": e["msg"]} for e in exc.errors()])

    async def _run(self, spec: ToolSpec, args: BaseModel) -> dict[str, Any]:
        if self._erp is None:
            return _err("SERVER", "Client ERP non configuré.")
        try:
            return await asyncio.wait_for(spec.handler(self._erp, args), timeout=self._timeout_s)
        except asyncio.TimeoutError:
            return _err("SERVER", f"L'outil {spec.name} n'a pas répondu à temps.")
        except Exception:  # noqa: BLE001 - un outil défaillant ne doit pas casser l'agent
            logger.exception("Erreur pendant l'exécution de l'outil %s", spec.name)
            return _err("SERVER", f"Erreur interne de l'outil {spec.name}.")

    async def execute_pending(self, action: PendingAction, role: str | None = None) -> dict[str, Any]:
        """Exécute une écriture confirmée (droits et arguments revérifiés)."""
        spec = self._specs[action.tool]
        role = role or (current_user.get().role if current_user.get() else None)
        if not self.allowed(spec, role):
            return _err("FORBIDDEN", f"Le rôle {role} n'a pas accès au module {spec.module}.")
        try:
            args = spec.args_model.model_validate(action.arguments)
        except ValidationError as exc:
            return self._invalid(exc)
        return {**await self._run(spec, args), "confirmed_action": action.public()}

    async def execute(self, name: str, raw_args: dict[str, Any] | None,
                      guard: ActionGuard | None = None) -> dict[str, Any]:
        """Valide puis exécute un outil. Ne lève jamais : retourne toujours un dict."""
        spec = self._specs.get(name)
        if spec is None:
            return _err("UNKNOWN_TOOL", f"Outil inconnu : {name}")
        user = current_user.get()
        role = user.role if user else None
        if not self.allowed(spec, role):
            return _err("FORBIDDEN", f"Le rôle {role} n'a pas accès au module {spec.module}.")
        try:
            args = spec.args_model.model_validate(raw_args or {})
        except ValidationError as exc:
            return self._invalid(exc)

        if spec.module == "system" or spec.requires_confirmation:
            if guard is None:
                return _err("CONFIRMATION_UNAVAILABLE", "Les écritures exigent une conversation active.")
            if name == "cancel_action":
                return guard.cancel(args.action_id)
            if name == "confirm_action":
                action = guard.take(args.action_id, by_user=False)
                return action if isinstance(action, dict) else await self.execute_pending(action, role)
            return guard.propose(name, args.model_dump(exclude_none=True), spec.summarize(args))
        return await self._run(spec, args)

    async def execute_timed(self, name: str, raw_args: dict[str, Any] | None,
                            guard: ActionGuard | None = None) -> tuple[dict[str, Any], float]:
        start = time.perf_counter()
        result = await self.execute(name, raw_args, guard)
        return result, (time.perf_counter() - start) * 1000
