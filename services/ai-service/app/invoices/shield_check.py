"""Après extraction : retrouver le fournisseur puis demander le contrôle Invoice Shield (rien n'est enregistré).

Tous les appels passent par la gateway avec le token de l'utilisateur (ErpClient + current_user).
"""
from __future__ import annotations

from typing import Any

from app.erp.client import ErpClient
from app.invoices.models import ExtractedInvoice

CHECK_PATH = "/api/stock/supplier-invoices/check"


async def match_supplier(erp: ErpClient, name: str | None) -> dict[str, Any]:
    if not name:
        return {"found": False, "message": "Nom du fournisseur non lu sur le document."}
    found = await erp.call("GET", "/api/stock/suppliers", params={"search": name})
    if not found.get("success"):
        return {"found": False, "message": found["error"]["message"]}
    items = found.get("data") or []
    exact = [s for s in items if str(s.get("name", "")).casefold() == name.casefold()]
    chosen = exact[0] if len(exact) == 1 else items[0] if len(items) == 1 else None
    if chosen is None:
        why = "Plusieurs fournisseurs correspondent" if items else "Fournisseur introuvable dans l'ERP"
        return {"found": False, "candidates": len(items), "message": f"{why} : « {name} »."}
    return {"found": True, "supplierId": chosen["id"], "name": chosen.get("name")}


def check_body(inv: ExtractedInvoice, supplier_id: int) -> dict[str, Any] | str:
    """Corps SupplierInvoiceRequest, ou message listant les champs obligatoires manquants."""
    missing = [f for f in ("invoiceNumber", "invoiceDate", "totalAmount") if getattr(inv, f) is None]
    if not inv.lines:
        missing.append("lines")
    if missing:
        return "Champs obligatoires non lus sur le document : " + ", ".join(missing) + "."
    lines = []
    for line in inv.lines:
        qty = max(1, round(line.quantity or 1))
        price = line.unitPrice if line.unitPrice is not None else (line.amount or 0) / qty
        label = (line.description or "Ligne sans libellé")[:255]  # limite du stock-service
        lines.append({"description": label, "quantity": qty, "unitPrice": price})
    body = {"supplierId": supplier_id, "invoiceNumber": inv.invoiceNumber, "invoiceDate": inv.invoiceDate,
            "bankAccount": inv.bankAccount, "lines": lines, "subtotal": inv.subtotal, "taxAmount": inv.taxAmount,
            "totalAmount": inv.totalAmount, "notes": inv.notes}
    return {k: v for k, v in body.items() if v is not None}


async def analyze(erp: ErpClient, inv: ExtractedInvoice) -> dict[str, Any]:
    """{extracted, supplierMatch, shield, [shieldError]} ; le rapport du shield est renvoyé tel quel."""
    out: dict[str, Any] = {"extracted": inv.model_dump(), "supplierMatch": await match_supplier(erp, inv.supplierName),
                           "shield": None}
    if not out["supplierMatch"]["found"]:
        return out
    body = check_body(inv, out["supplierMatch"]["supplierId"])
    if isinstance(body, str):
        return {**out, "shieldError": {"code": "VALIDATION", "message": body}}
    report = await erp.call("POST", CHECK_PATH, json=body)
    if not report.get("success"):
        return {**out, "shieldError": report["error"]}
    return {**out, "shield": {k: v for k, v in report.items() if k != "success"}}
