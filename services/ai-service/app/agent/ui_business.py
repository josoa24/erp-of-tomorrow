from __future__ import annotations

from typing import Any

from app.agent.ui_cells import (EMPLOYEE_LABELS, EMPLOYEE_TONES, PAYMENT_LABELS, PAYMENT_TONES, column, kpi, pick,
                                rows_of, status, table)


def sales_summary(result: dict[str, Any]) -> list[dict[str, Any]]:
    return kpi("Ventes", [("Chiffre d'affaires", result.get("totalRevenue"), None, "money"),
                          ("Ventes", result.get("salesCount"), None, "number"),
                          ("Panier moyen", result.get("averageBasket"), None, "money")])


def top_products(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [pick(r, "productName", "totalQuantity", "totalRevenue") for r in rows_of(result)]
    return table("Produits les plus vendus", [column("productName", "Produit"),
                                              column("totalQuantity", "Quantité", "number"),
                                              column("totalRevenue", "Chiffre d'affaires", "money")], rows, result)


def sales(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{**pick(r, "number", "customerName", "totalAmount"),
             "payment": status(r.get("paymentStatus"), PAYMENT_LABELS, PAYMENT_TONES)} for r in rows_of(result)]
    return table("Ventes", [column("number", "N°"), column("customerName", "Client"),
                            column("totalAmount", "Total", "money"), column("payment", "Paiement", "status")],
                 rows, result)


def customers(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [pick(r, "name", "city", "phone", "type") for r in rows_of(result)]
    return table("Clients", [column("name", "Client"), column("city", "Ville"), column("phone", "Téléphone"),
                             column("type", "Type")], rows, result)


def customer_profile(result: dict[str, Any]) -> list[dict[str, Any]]:
    customer = result.get("customer") or {}
    fields = [("Achats", result.get("salesCount")), ("Total acheté", result.get("totalPurchased")),
              ("Panier moyen", result.get("averageBasket")), ("Ville", customer.get("city")),
              ("Téléphone", customer.get("phone"))]
    return [{"type": "card", "title": customer.get("name", "Client"), "tone": "info",
             "subtitle": customer.get("type") or "", "fields": [{"label": k, "value": v} for k, v in fields if v is not None]}]


def hr_summary(result: dict[str, Any]) -> list[dict[str, Any]]:
    return kpi("Équipe", [("Effectif", result.get("totalEmployees"), None, "number"),
                          ("En congé aujourd'hui", result.get("onLeaveToday"), None, "number"),
                          ("Demandes à valider", result.get("pendingLeaves"), None, "number"),
                          ("Services", result.get("departmentsWithEmployees"), None, "number")])


def employees(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{"name": f"{r.get('firstName', '')} {r.get('lastName', '')}".strip(), **pick(r, "position", "departmentName"),
             "state": status(r.get("status"), EMPLOYEE_LABELS, EMPLOYEE_TONES)} for r in rows_of(result)]
    return table("Employés", [column("name", "Employé"), column("position", "Poste"),
                              column("departmentName", "Service"), column("state", "Statut", "status")], rows, result)
