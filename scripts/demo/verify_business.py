"""Contrôles métier, module par module, sur les données créées par seed_demo.py."""
from __future__ import annotations

from api import Api
from checks import Checks


def verify_stock(c: Checks, stock: Api) -> None:
    c.section("Stock : produits, prévision, fournisseurs")
    products = {p["sku"]: p for p in c.status("liste des produits", stock, "GET", "/api/stock/products", 200)}
    rice = products["RIZ-25"]
    c.status("état de stock trié par risque", stock, "GET", "/api/stock/status", 200)
    summary = c.status("résumé du stock", stock, "GET", "/api/stock/summary", 200)
    c.expect("valeur du stock positive", lambda: float(summary["totalStockValue"]) > 0, lambda: summary)
    forecast = c.status("prévision du riz", stock, "GET", f"/api/stock/products/{rice['id']}/replenishment", 200)
    c.expect("historique suffisant (confidence OK)", lambda: forecast["confidence"] == "OK", lambda: forecast)
    c.expect("le riz est à recommander, avec une quantité", lambda: forecast["needsReorder"]
             and forecast["recommendedQuantity"] > 0, lambda: forecast)
    c.expect("délai pris chez le fournisseur", lambda: forecast["leadTimeSource"] == "SUPPLIER", lambda: forecast)
    needed = c.status("produits à commander", stock, "GET", "/api/stock/replenishment", 200)
    c.expect("le riz est dans la liste à commander", lambda: any(f["productId"] == rice["id"] for f in needed))
    suppliers = c.status("fournisseurs", stock, "GET", "/api/stock/suppliers", 200)
    c.expect("compte bancaire jamais en clair", lambda: all("bankAccount" not in s for s in suppliers))
    c.status("fiche 360 du fournisseur", stock, "GET", f"/api/stock/suppliers/{suppliers[0]['id']}/profile", 200)


def verify_invoice_shield(c: Checks, stock: Api, admin: Api) -> None:
    c.section("Invoice Shield")
    invoices = {i["invoiceNumber"]: i for i in c.status("factures", stock, "GET", "/api/stock/supplier-invoices", 200)}
    clean, trapped = invoices.get("FA-2026-0418"), invoices.get("FA-2026-0419")
    c.expect("facture saine acceptée (CLEARED)", lambda: clean["status"] == "CLEARED", lambda: clean)
    codes = {a["code"] for a in trapped["alerts"]}
    c.expect("facture piégée bloquée (BLOCKED)", lambda: trapped["status"] == "BLOCKED", lambda: trapped)
    c.expect("RIB différent, injection, totaux et surfacturation détectés", lambda: {
        "BANK_ACCOUNT_MISMATCH", "PROMPT_INJECTION", "TOTALS_MISMATCH", "OVER_INVOICED"} <= codes, lambda: codes)
    c.status("STOCK ne peut pas forcer la facture bloquée", stock, "POST",
             f"/api/stock/supplier-invoices/{trapped['id']}/approve", 403, {"justification": "urgent"})
    history = c.status("historique d'audit", stock, "GET", f"/api/stock/supplier-invoices/{trapped['id']}/history", 200)
    c.expect("création et contrôle tracés", lambda: [e["type"] for e in history][:2] == ["CREATED", "CHECKED"])


def verify_sales(c: Checks, sales: Api) -> None:
    c.section("Vente")
    summary = c.status("chiffre d'affaires", sales, "GET", "/api/vente/sales/summary", 200)
    c.expect("chiffre d'affaires réparti sur plusieurs jours", lambda: len(summary["daily"]) > 10, lambda: summary)
    top = c.status("meilleurs produits", sales, "GET", "/api/vente/sales/top-products", 200)
    c.expect("le riz est en tête des ventes", lambda: "Riz" in top[0]["productName"], lambda: top[:2])
    customers = c.status("clients", sales, "GET", "/api/vente/customers", 200)
    c.status("fiche 360 du client", sales, "GET", f"/api/vente/customers/{customers[0]['id']}/profile", 200)


def verify_hr_and_crm(c: Checks, hr: Api, crm: Api) -> None:
    c.section("RH et CRM")
    summary = c.status("résumé RH", hr, "GET", "/api/rh/summary", 200)
    c.expect("4 employés", lambda: summary["totalEmployees"] == 4, lambda: summary)
    employees = c.status("employés", hr, "GET", "/api/rh/employees", 200)
    c.expect("3 employés liés à leur compte", lambda: sum(1 for e in employees if e.get("userId")) == 3)
    c.status("congés", hr, "GET", "/api/rh/leaves", 200)
    c.status("pointages", hr, "GET", "/api/rh/attendances", 200)
    timeline = c.status("chronologie d'un client", crm, "GET", "/api/crm/parties/CUSTOMER/1/timeline", 200)
    kinds = {event["kind"] for event in timeline}
    c.expect("échanges et ventes dans la chronologie", lambda: len(kinds) >= 2, lambda: kinds)
