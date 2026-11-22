from __future__ import annotations

from typing import Any

from app.agent.ui_cells import (ORDER_LABELS, ORDER_TONES, RISK_LABELS, RISK_TONES, column, kpi, pick, rows_of,
                                status, table)


def stock_summary(result: dict[str, Any]) -> list[dict[str, Any]]:
    rate = result.get("availabilityRate")
    return kpi("Synthèse du stock", [
        ("Produits", result.get("totalProducts"), None, "number"),
        ("En alerte", result.get("lowStockProducts"), None, "number"),
        ("Disponibilité", None if rate is None else round(rate * 100, 1), None, "percent"),
        ("Valeur du stock", result.get("totalStockValue"), None, "money")])


def products(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{**pick(r, "name", "sku", "quantity", "minThreshold", "unitPrice"),
             "state": status("CRITICAL" if r.get("lowStock") else "OK", {"CRITICAL": "En alerte", "OK": "Correct"},
                             RISK_TONES)} for r in rows_of(result)]
    return table("Produits", [column("name", "Produit"), column("sku", "SKU"), column("quantity", "Stock", "number"),
                              column("minThreshold", "Seuil", "number"), column("unitPrice", "Prix", "money"),
                              column("state", "État", "status")], rows, result)


def stock_status(result: dict[str, Any]) -> list[dict[str, Any]]:
    items = rows_of(result)
    rows = [{**pick(r, "productName", "quantity", "daysRemaining"), "risk": status(r.get("risk"), RISK_LABELS, RISK_TONES)}
            for r in items]
    alerts = [{"title": r["productName"], "tone": RISK_TONES.get(r.get("risk"), "neutral"),
               "detail": f"{r.get('quantity')} en stock, environ {r.get('daysRemaining')} jours restants"}
              for r in items if r.get("risk") in ("CRITICAL", "WARNING")][:5]
    blocks = table("État du stock", [column("productName", "Produit"), column("quantity", "Stock", "number"),
                                     column("daysRemaining", "Jours restants", "number"),
                                     column("risk", "Risque", "status")], rows, result)
    return ([{"type": "alerts", "title": "Produits à surveiller", "items": alerts}] if alerts else []) + blocks


def autonomy(result: dict[str, Any]) -> list[dict[str, Any]]:
    risk = result.get("risk")
    fields = [("Stock actuel", result.get("currentStock")), ("Vente moyenne par jour", result.get("averageDailyOut")),
              ("Jours restants", result.get("daysRemaining"))]
    return [{"type": "card", "title": result.get("productName", "Produit"), "tone": RISK_TONES.get(risk, "neutral"),
             "subtitle": RISK_LABELS.get(risk, ""), "fields": [{"label": k, "value": v} for k, v in fields if v is not None]}]


def movements(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{**pick(r, "quantity", "stockAfter", "reason", "createdAt"),
             "type": status(r.get("type"), {"IN": "Entrée", "OUT": "Sortie"}, {"IN": "success", "OUT": "warning"})}
            for r in rows_of(result)]
    return table("Derniers mouvements", [column("type", "Type", "status"), column("quantity", "Quantité", "number"),
                                         column("stockAfter", "Stock après", "number"), column("reason", "Motif"),
                                         column("createdAt", "Date", "date")], rows, result)


def suppliers(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [pick(r, "name", "phone", "city", "usualDeliveryDays") for r in rows_of(result)]
    return table("Fournisseurs", [column("name", "Fournisseur"), column("phone", "Téléphone"), column("city", "Ville"),
                                  column("usualDeliveryDays", "Délai (jours)", "number")], rows, result)


def purchase_orders(result: dict[str, Any]) -> list[dict[str, Any]]:
    rows = [{**pick(r, "id", "totalAmount", "createdAt"), "status": status(r.get("status"), ORDER_LABELS, ORDER_TONES)}
            for r in rows_of(result)]
    return table("Bons de commande", [column("id", "N°", "number"), column("status", "Statut", "status"),
                                      column("totalAmount", "Total", "money"), column("createdAt", "Créé le", "date")],
                 rows, result)
