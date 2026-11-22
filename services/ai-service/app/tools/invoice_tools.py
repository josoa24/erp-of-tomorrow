"""Outils Invoice Shield (rôles STOCK, ADMIN) -> /api/stock/supplier-invoices via la gateway.

- check_supplier_invoice : contrôle anti-fraude SANS rien enregistrer (lecture).
- register_supplier_invoice : enregistrement, soumis à la confirmation de l'utilisateur. Le stock-service
  relance le contrôle ; une facture BLOCKED est signalée comme telle, jamais présentée comme sûre.
"""
from __future__ import annotations

from typing import Any

from app.erp.client import ErpClient
from app.tools.invoice_args import INVOICE_PROPERTIES, INVOICE_REQUIRED, InvoiceArgs, invoice_body
from app.tools.spec import WRITE, ToolSpec, decl

BLOCKED_WARNING = ("Facture BLOQUÉE par le contrôle anti-fraude : ne pas la payer et ne pas la présenter "
                   "comme sûre. Seul un ADMIN peut l'approuver avec une justification.")


def flag_blocked(result: dict[str, Any]) -> dict[str, Any]:
    """Ajoute un avertissement explicite si le verdict (ou le statut) est BLOCKED."""
    if result.get("verdict") == "BLOCKED" or result.get("status") == "BLOCKED":
        return {**result, "warning": BLOCKED_WARNING}
    return result


async def check_supplier_invoice(erp: ErpClient, a: InvoiceArgs) -> dict[str, Any]:
    return flag_blocked(await erp.call("POST", "/api/stock/supplier-invoices/check", json=invoice_body(a)))


async def register_supplier_invoice(erp: ErpClient, a: InvoiceArgs) -> dict[str, Any]:
    return flag_blocked(await erp.call("POST", "/api/stock/supplier-invoices", json=invoice_body(a)))


def _register_summary(a: InvoiceArgs) -> str:
    """Récapitulatif sans le compte bancaire (donnée sensible)."""
    total = f"{a.total_amount:,.0f}".replace(",", " ")
    return (f"Enregistrer la facture {a.invoice_number} du {a.invoice_date} (fournisseur #{a.supplier_id}, "
            f"{len(a.lines)} ligne(s), total {total} Ar). Le contrôle anti-fraude sera relancé ; "
            "une facture BLOCKED ne pourra pas être payée.")


SPECS = [
    ToolSpec(decl("check_supplier_invoice", "Contrôle anti-fraude d'une facture fournisseur sans l'enregistrer "
                  "(doublon, compte bancaire, totaux, bon de commande, injection). Renvoie verdict CLEAR, WARNING "
                  "ou BLOCKED et les alertes.", INVOICE_PROPERTIES, INVOICE_REQUIRED),
             InvoiceArgs, check_supplier_invoice, "stock"),
    ToolSpec(decl("register_supplier_invoice", "Enregistre une facture fournisseur (contrôle relancé par l'ERP). "
                  "Appeler check_supplier_invoice avant ; si BLOCKED, le dire clairement." + WRITE,
                  INVOICE_PROPERTIES, INVOICE_REQUIRED),
             InvoiceArgs, register_supplier_invoice, "stock", _register_summary),
]
