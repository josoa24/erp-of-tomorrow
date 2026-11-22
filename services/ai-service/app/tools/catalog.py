"""Catalogue des outils (liste blanche) et règle d'accès par rôle, identique à la gateway."""
from __future__ import annotations

from pydantic import Field

from app.tools import invoice_tools, rh_crm_tools, stock_tools, vente_tools
from app.tools.spec import Args, ToolSpec, decl, string

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
ALL_SPECS: list[ToolSpec] = (stock_tools.SPECS + invoice_tools.SPECS + vente_tools.SPECS + rh_crm_tools.SPECS
                             + SYSTEM_SPECS)


def module_allowed(module: str, role: str | None) -> bool:
    if module == "system" or role == "ADMIN":
        return True
    return role in MODULE_ROLES.get(module, set())
