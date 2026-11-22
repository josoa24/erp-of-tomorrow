"""Catalogue des outils (liste blanche) et règle d'accès par rôle, identique à la gateway."""
from __future__ import annotations

from typing import Iterable

from pydantic import Field

from app.erp.context import normalize_roles

from app.tools import (briefing_tools, crm_tools, invoice_tools, module_tools, replenishment_tools, rh_advance_tools,
                       rh_crm_tools, rh_leave_tools, stock_tools, vente_credit_tools, vente_tools)
from app.tools.spec import Args, ToolSpec, decl, string

# Même règle que la gateway (RoleAccessPolicy) : un rôle = un module, ADMIN = tout, union des rôles.
OPEN_MODULES = {"system", "general"}
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
ALL_SPECS: list[ToolSpec] = (stock_tools.SPECS + replenishment_tools.SPECS + invoice_tools.SPECS
                             + vente_tools.SPECS + vente_credit_tools.SPECS + rh_crm_tools.SPECS
                             + rh_leave_tools.SPECS + rh_advance_tools.SPECS + crm_tools.SPECS
                             + module_tools.SPECS + briefing_tools.SPECS + SYSTEM_SPECS)


def module_allowed(module: str, roles: str | Iterable[str] | None) -> bool:
    held = set(normalize_roles(roles))
    if module in OPEN_MODULES or "ADMIN" in held:
        return True
    return bool(held & MODULE_ROLES.get(module, set()))
