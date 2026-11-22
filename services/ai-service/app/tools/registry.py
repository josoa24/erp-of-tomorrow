"""Registre des outils : liste blanche, filtrage par rôle, validation, confirmation des écritures.

- Gemini ne reçoit QUE les outils des modules autorisés pour le rôle de l'utilisateur
  (moins d'outils = réponses plus rapides, et pas de tentative inutile).
- La gateway de l'ERP revérifie de toute façon chaque appel avec le token (défense en profondeur).
- Aucun outil d'écriture n'est exécuté sans confirmation (voir app/agent/confirmation.py).
"""
from __future__ import annotations

import time
from typing import Any

from google.genai import types
from pydantic import ValidationError

from app.agent.confirmation import ActionGuard, PendingAction
from app.erp.client import ErpClient
from app.erp.context import current_user
from app.tools.catalog import ALL_SPECS, module_allowed
from app.tools.runner import invalid_args, run_spec, tool_error
from app.tools.spec import ToolSpec


class ToolRegistry:
    def __init__(self, erp: ErpClient | None, timeout_s: float = 10.0) -> None:
        self._erp = erp
        self._timeout_s = timeout_s
        self._specs: dict[str, ToolSpec] = {s.name: s for s in ALL_SPECS}
        self._tool_cache: dict[str, types.Tool] = {}

    @staticmethod
    def allowed(spec: ToolSpec, role: str | None) -> bool:
        return module_allowed(spec.module, role)

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

    def _forbidden(self, spec: ToolSpec, role: str | None) -> dict[str, Any]:
        return tool_error("FORBIDDEN", f"Le rôle {role} n'a pas accès au module {spec.module}.")

    async def execute_pending(self, action: PendingAction, role: str | None = None) -> dict[str, Any]:
        """Exécute une écriture confirmée (droits et arguments revérifiés)."""
        spec = self._specs[action.tool]
        role = role or (current_user.get().role if current_user.get() else None)
        if not self.allowed(spec, role):
            return self._forbidden(spec, role)
        try:
            args = spec.args_model.model_validate(action.arguments)
        except ValidationError as exc:
            return invalid_args(exc)
        return {**await run_spec(self._erp, spec, args, self._timeout_s), "confirmed_action": action.public()}

    async def execute(self, name: str, raw_args: dict[str, Any] | None,
                      guard: ActionGuard | None = None) -> dict[str, Any]:
        """Valide puis exécute un outil. Ne lève jamais : retourne toujours un dict."""
        spec = self._specs.get(name)
        if spec is None:
            return tool_error("UNKNOWN_TOOL", f"Outil inconnu : {name}")
        user = current_user.get()
        role = user.role if user else None
        if not self.allowed(spec, role):
            return self._forbidden(spec, role)
        try:
            args = spec.args_model.model_validate(raw_args or {})
        except ValidationError as exc:
            return invalid_args(exc)
        if spec.module == "system" or spec.requires_confirmation:
            if guard is None:
                return tool_error("CONFIRMATION_UNAVAILABLE", "Les écritures exigent une conversation active.")
            if name == "cancel_action":
                return guard.cancel(args.action_id)
            if name == "confirm_action":
                action = guard.take(args.action_id, by_user=False)
                return action if isinstance(action, dict) else await self.execute_pending(action, role)
            return guard.propose(name, args.model_dump(exclude_none=True), spec.summarize(args))
        return await run_spec(self._erp, spec, args, self._timeout_s)

    async def execute_timed(self, name: str, raw_args: dict[str, Any] | None,
                            guard: ActionGuard | None = None) -> tuple[dict[str, Any], float]:
        start = time.perf_counter()
        result = await self.execute(name, raw_args, guard)
        return result, (time.perf_counter() - start) * 1000
