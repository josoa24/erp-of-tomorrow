from __future__ import annotations

from typing import Any

from app.agent.ui_cells import column, kpi, pick, rows_of, status, table

CONTACT_LABELS = {"ACTIVE": "Actif", "FOLLOW_UP": "À relancer"}
CONTACT_TONES = {"ACTIVE": "success", "FOLLOW_UP": "warning"}
LEAVE_LABELS = {"DRAFT": "À valider", "VALIDATED": "Validé", "REJECTED": "Refusé"}
LEAVE_TONES = {"DRAFT": "warning", "VALIDATED": "success", "REJECTED": "neutral"}


def contacts(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{**pick(r, "name", "typeLabel", "city", "phone"),
             "state": status(r.get("status"), CONTACT_LABELS, CONTACT_TONES)} for r in rows_of(result)]
    return table("Contacts", [column("name", "Contact"), column("typeLabel", "Type"), column("city", "Ville"),
                              column("phone", "Téléphone"), column("state", "Statut", "status")], rows, result)


def contact_summary(result: dict[str, Any]) -> list[dict[str, Any]]:
    return kpi("Relations", [("Clients actifs", result.get("activeCustomers"), None, "number"),
                             ("Fournisseurs", result.get("suppliers"), None, "number"),
                             ("À relancer", result.get("followUp"), None, "number"),
                             ("Encours crédit", result.get("creditOutstanding"), None, "money")])


def leaves(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{**pick(r, "employeeName", "startDate", "endDate", "days"),
             "state": status(r.get("status"), LEAVE_LABELS, LEAVE_TONES)} for r in rows_of(result)]
    return table("Demandes de congé", [column("employeeName", "Employé"), column("startDate", "Du"),
                                       column("endDate", "Au"), column("days", "Jours", "number"),
                                       column("state", "Statut", "status")], rows, result)


def advances(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{**pick(r, "employeeName", "amount"), "state": status(r.get("status"), LEAVE_LABELS, LEAVE_TONES)}
            for r in rows_of(result)]
    return table("Avances sur salaire", [column("employeeName", "Employé"), column("amount", "Montant", "money"),
                                         column("state", "Statut", "status")], rows, result)


def advance_summary(result: dict[str, Any]) -> list[dict[str, Any]]:
    return kpi("Avances du mois", [("Total", result.get("monthTotal"), None, "money"),
                                   ("Demandes", result.get("requests"), None, "number"),
                                   ("À valider", result.get("pending"), None, "number")])


def sales_kpis(result: dict[str, Any]) -> list[dict[str, Any]]:
    return kpi("Ventes", [("Aujourd'hui", result.get("todayRevenue"), None, "money"),
                          ("Encaissé", result.get("todayCollected"), None, "money"),
                          ("Ce mois", result.get("monthRevenue"), None, "money"),
                          ("Panier moyen", result.get("averageBasket"), None, "money")])


def credit_summary(result: dict[str, Any]) -> list[dict[str, Any]]:
    return kpi("Crédit clients", [("Encours", result.get("outstanding"), None, "money"),
                                  ("Clients", result.get("customers"), None, "number"),
                                  ("En retard", result.get("overdueCustomers"), None, "number")])


def module_indicators(result: dict[str, Any]) -> list[dict[str, Any]]:
    items = [(r.get("label"), r.get("value") if r.get("total") is None else f"{r.get('value')} / {r.get('total')}",
              ", ".join(r.get("detail") or []) or None, "money" if r.get("money") else "number")
             for r in rows_of(result)]
    return kpi("Indicateurs du module", items)
