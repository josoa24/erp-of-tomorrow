from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.erp.client import ErpClient, keep
from app.tools.spec import WRITE, Args, ToolSpec, decl, integer, string


class LeaveListArgs(Args):
    status: Literal["DRAFT", "VALIDATED", "REJECTED"] | None = None
    employee_id: int | None = Field(default=None, ge=1)


class LeaveRequestArgs(Args):
    employee_id: int = Field(ge=1)
    start_date: str = Field(pattern=r"^\d{4}-\d{2}-\d{2}$")
    end_date: str = Field(pattern=r"^\d{4}-\d{2}-\d{2}$")
    reason: str | None = Field(default=None, max_length=500)


class LeaveIdArgs(Args):
    leave_id: int = Field(ge=1)


async def list_leave_requests(erp: ErpClient, a: LeaveListArgs):
    return keep(await erp.call("GET", "/api/rh/leaves", params={"status": a.status, "employeeId": a.employee_id}), 30,
                "startDate")


async def request_leave(erp: ErpClient, a: LeaveRequestArgs):
    body = {"employeeId": a.employee_id, "startDate": a.start_date, "endDate": a.end_date, "reason": a.reason}
    return await erp.call("POST", "/api/rh/leaves", json={k: v for k, v in body.items() if v is not None})


async def validate_leave(erp: ErpClient, a: LeaveIdArgs):
    return await erp.call("POST", f"/api/rh/leaves/{a.leave_id}/validate")


async def reject_leave(erp: ErpClient, a: LeaveIdArgs):
    return await erp.call("POST", f"/api/rh/leaves/{a.leave_id}/reject")


_ID = {"leave_id": integer("Id de la demande de congé.")}

SPECS = [
    ToolSpec(decl("list_leave_requests", "RH : demandes de congé avec nom de l'employé, dates, jours et statut "
                  "(DRAFT = à valider).", {"status": string("Statut.", ["DRAFT", "VALIDATED", "REJECTED"]),
                                           "employee_id": integer("Filtre employé.")}),
             LeaveListArgs, list_leave_requests, "rh"),
    ToolSpec(decl("request_leave", "RH : crée une demande de congé en brouillon (solde vérifié par l'ERP)." + WRITE,
                  {"employee_id": integer("Id employé."), "start_date": string("AAAA-MM-JJ."),
                   "end_date": string("AAAA-MM-JJ."), "reason": string("Motif.")},
                  ["employee_id", "start_date", "end_date"]), LeaveRequestArgs, request_leave, "rh",
             lambda a: f"Demander un congé pour l'employé #{a.employee_id} du {a.start_date} au {a.end_date}."),
    ToolSpec(decl("validate_leave", "RH : valide un congé (décompte le solde, devient non modifiable)." + WRITE,
                  _ID, ["leave_id"]), LeaveIdArgs, validate_leave, "rh",
             lambda a: f"Valider la demande de congé #{a.leave_id} (décision définitive)."),
    ToolSpec(decl("reject_leave", "RH : refuse un congé (décision définitive)." + WRITE, _ID, ["leave_id"]),
             LeaveIdArgs, reject_leave, "rh", lambda a: f"Refuser la demande de congé #{a.leave_id} (décision définitive)."),
]
