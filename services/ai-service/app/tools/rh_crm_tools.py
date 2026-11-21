"""Outils RH (rôles RH, ADMIN) et CRM (rôles CRM, ADMIN), en lecture seule."""
from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.erp.client import ErpClient, keep
from app.tools.spec import Args, NoArgs, ToolSpec, decl, integer, string


class EmployeeSearchArgs(Args):
    search: str | None = Field(default=None, max_length=100)
    department_id: int | None = Field(default=None, ge=1)


class PartyArgs(Args):
    party_type: Literal["CUSTOMER", "SUPPLIER"]
    party_id: int = Field(ge=1)


async def get_hr_summary(erp: ErpClient, a: NoArgs):
    return await erp.call("GET", "/api/rh/summary")


async def search_employees(erp: ErpClient, a: EmployeeSearchArgs):
    return keep(await erp.call("GET", "/api/rh/employees",
                               params={"search": a.search, "departmentId": a.department_id}), 50)


async def get_party_timeline(erp: ErpClient, a: PartyArgs):
    return keep(await erp.call("GET", f"/api/crm/parties/{a.party_type}/{a.party_id}/timeline"), 20)


SPECS = [
    ToolSpec(decl("get_hr_summary", "RH : effectif, effectif par département, personnes en congé aujourd'hui."),
             NoArgs, get_hr_summary, "rh"),
    ToolSpec(decl("search_employees", "RH : employés (nom, département, solde de congés).",
                  {"search": string("Nom, prénom ou email."), "department_id": integer("Filtre département.")}),
             EmployeeSearchArgs, search_employees, "rh"),
    ToolSpec(decl("get_party_timeline", "CRM : chronologie d'un client ou fournisseur (échanges, ventes, commandes).",
                  {"party_type": string("Type de tiers.", ["CUSTOMER", "SUPPLIER"]), "party_id": integer("Id du tiers.")},
                  ["party_type", "party_id"]), PartyArgs, get_party_timeline, "crm"),
]
