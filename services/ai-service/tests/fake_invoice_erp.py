"""Fausse gateway avec l'API Invoice Shield du stock-service (contrat /api/stock/supplier-invoices)."""
from __future__ import annotations

import httpx

from tests.fake_erp import FakeErp, err

INJECTION_ALERT = {"code": "PROMPT_INJECTION", "severity": "BLOCKING",
                   "message": "Le document contient des instructions adressées à un assistant."}


class FakeInvoiceErp(FakeErp):
    def __init__(self) -> None:
        super().__init__()
        self.suppliers = [{"id": 1, "name": "Riz Import SARL", "bankAccountLast4": "2345"},
                          {"id": 2, "name": "Huilerie du Sud", "bankAccountLast4": None}]
        self.invoices: list[dict] = []

    def report(self, body: dict) -> dict:
        alerts = []
        if "ignore" in (body.get("notes") or "").lower():
            alerts.append(INJECTION_ALERT)
        if body.get("bankAccount") and not body["bankAccount"].replace(" ", "").endswith("2345"):
            alerts.append({"code": "BANK_ACCOUNT_MISMATCH", "severity": "BLOCKING", "message": "Compte différent."})
        verdict = "BLOCKED" if any(a["severity"] == "BLOCKING" for a in alerts) else "CLEAR"
        return {"verdict": verdict, "alerts": alerts}

    def route(self, method: str, path: str, q: dict, body) -> httpx.Response:
        if path == "/api/stock/supplier-invoices/check" and method == "POST":
            if not body.get("lines") or "totalAmount" not in body:
                return err(400, "VALIDATION", "lines et totalAmount sont obligatoires")
            return httpx.Response(200, json=self.report(body))
        if path == "/api/stock/supplier-invoices" and method == "POST":
            rep = self.report(body)
            inv = {"id": len(self.invoices) + 1, "supplierId": body["supplierId"],
                   "invoiceNumber": body["invoiceNumber"], "totalAmount": body["totalAmount"],
                   "status": "BLOCKED" if rep["verdict"] == "BLOCKED" else "CLEARED", **rep,
                   "bankAccountLast4": (body.get("bankAccount") or "")[-4:] or None}
            self.invoices.append(inv)
            return httpx.Response(201, json=inv)
        return super().route(method, path, q, body)
