from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.erp.client import ErpClient, keep
from app.tools.spec import WRITE, Args, NoArgs, S, T, ToolSpec, decl, integer, string


class AdvanceListArgs(Args):
    status: Literal["DRAFT", "VALIDATED", "REJECTED"] | None = None
    employee_id: int | None = Field(default=None, ge=1)


class AdvanceRequestArgs(Args):
    employee_id: int = Field(ge=1)
    amount: float = Field(gt=0)
    reason: str | None = Field(default=None, max_length=500)


class AdvanceIdArgs(Args):
    advance_id: int = Field(ge=1)


async def list_salary_advances(erp: ErpClient, a: AdvanceListArgs):
    return keep(await erp.call("GET", "/api/rh/advances", params={"status": a.status, "employeeId": a.employee_id}), 30)


async def get_advance_summary(erp: ErpClient, a: NoArgs):
    return await erp.call("GET", "/api/rh/advances/summary")


async def request_salary_advance(erp: ErpClient, a: AdvanceRequestArgs):
    body = {"employeeId": a.employee_id, "amount": a.amount, "reason": a.reason}
    return await erp.call("POST", "/api/rh/advances", json={k: v for k, v in body.items() if v is not None})


async def validate_salary_advance(erp: ErpClient, a: AdvanceIdArgs):
    return await erp.call("POST", f"/api/rh/advances/{a.advance_id}/validate")


async def reject_salary_advance(erp: ErpClient, a: AdvanceIdArgs):
    return await erp.call("POST", f"/api/rh/advances/{a.advance_id}/reject")


_ID = {"advance_id": integer("Id de l'avance.")}
_ADMIN = " Réservé au rôle ADMIN (l'ERP refuse sinon)."

SPECS = [
    ToolSpec(decl("list_salary_advances", "RH : avances sur salaire du mois (DRAFT = à valider).",
                  {"status": string("Statut.", ["DRAFT", "VALIDATED", "REJECTED"]),
                   "employee_id": integer("Filtre employé.")}), AdvanceListArgs, list_salary_advances, "rh"),
    ToolSpec(decl("get_advance_summary", "RH : total des avances du mois, nombre de demandes et à valider."),
             NoArgs, get_advance_summary, "rh"),
    ToolSpec(decl("request_salary_advance", "RH : demande une avance sur salaire. Plafond : 30 % du salaire par mois "
                  "(vérifié par l'ERP)." + WRITE, {"employee_id": integer("Id employé."),
                                                   "amount": S(type=T.NUMBER, description="Montant en Ar."),
                                                   "reason": string("Motif.")}, ["employee_id", "amount"]),
             AdvanceRequestArgs, request_salary_advance, "rh",
             lambda a: f"Demander une avance de {a.amount:,.0f} Ar pour l'employé #{a.employee_id}.".replace(",", " ")),
    ToolSpec(decl("validate_salary_advance", "RH : valide une avance." + _ADMIN + WRITE, _ID, ["advance_id"]),
             AdvanceIdArgs, validate_salary_advance, "rh",
             lambda a: f"Valider l'avance #{a.advance_id} (décision définitive)."),
    ToolSpec(decl("reject_salary_advance", "RH : refuse une avance." + _ADMIN + WRITE, _ID, ["advance_id"]),
             AdvanceIdArgs, reject_salary_advance, "rh",
             lambda a: f"Refuser l'avance #{a.advance_id} (décision définitive)."),
]
