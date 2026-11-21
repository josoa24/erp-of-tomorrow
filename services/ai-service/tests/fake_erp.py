"""Fausse gateway ERP (httpx.MockTransport) : mêmes routes, mêmes règles de rôle, même format d'erreur.

Jeton de test : "tok-<ROLE>" (ex. tok-STOCK). Les données vivent dans l'instance (mutables).
"""
from __future__ import annotations

import json
import re

import httpx

MODULE_ROLES = {"stock": "STOCK", "vente": "VENTE", "rh": "RH", "crm": "CRM"}


def err(status: int, type_: str, message: str) -> httpx.Response:
    return httpx.Response(status, json={"status": status, "type": type_, "message": message,
                                        "timestamp": "2026-11-21T12:00:00Z"})


class FakeErp:
    def __init__(self) -> None:
        self.requests: list[httpx.Request] = []
        self.products = {1: {"id": 1, "name": "Riz blanc 25 kg", "sku": "RIZ-25", "unit": "sac", "quantity": 14,
                             "minThreshold": 20, "unitPrice": 45000, "lowStock": True},
                         2: {"id": 2, "name": "Huile 5 L", "sku": "HUI-5", "unit": "bidon", "quantity": 60,
                             "minThreshold": 10, "unitPrice": 32000, "lowStock": False}}
        self.sales: list[dict] = []
        self.users: dict[str, str] = {"admin@erp.local": "ADMIN"}
        self.suppliers: list[dict] = []
        self.customers: list[dict] = []

    def writes(self) -> list[httpx.Request]:
        return [r for r in self.requests if r.method in ("POST", "PUT", "DELETE")]

    def http(self) -> httpx.AsyncClient:
        return httpx.AsyncClient(base_url="http://gateway.test", transport=httpx.MockTransport(self.handle))

    def handle(self, request: httpx.Request) -> httpx.Response:
        self.requests.append(request)
        path, q = request.url.path, dict(request.url.params)
        auth = request.headers.get("authorization", "")
        if not auth.startswith("Bearer tok-"):
            return err(401, "UNAUTHORIZED", "Authentification requise")
        role = auth[len("Bearer tok-"):].split(".")[0]
        module = path.split("/")[2] if path.count("/") >= 2 else ""
        if role != "ADMIN" and (module == "auth" or MODULE_ROLES.get(module) != role):
            return err(403, "FORBIDDEN", f"Le rôle {role} n'a pas accès à ce module")
        return self.route(request.method, path, q, json.loads(request.content) if request.content else None)

    def route(self, method: str, path: str, q: dict, body) -> httpx.Response:
        if path == "/api/stock/products" and method == "GET":
            term = q.get("search", "").lower()
            items = [p for p in self.products.values()
                     if term in p["name"].lower() or term in p["sku"].lower()]
            return httpx.Response(200, json=items)
        if path == "/api/stock/products" and method == "POST":
            if any(p["sku"] == body["sku"] for p in self.products.values()):
                return err(409, "BUSINESS", "SKU déjà utilisé")
            pid = max(self.products, default=0) + 1
            self.products[pid] = {"id": pid, **body, "lowStock": body["quantity"] <= body["minThreshold"]}
            return httpx.Response(201, json=self.products[pid])
        if path == "/api/auth/users" and method == "POST":
            if body["email"] in self.users:
                return err(409, "BUSINESS", "Email déjà utilisé")
            self.users[body["email"]] = body["role"]
            return httpx.Response(201, json={"id": len(self.users), "email": body["email"], "role": body["role"]})
        for kind, store in (("stock/suppliers", self.suppliers), ("vente/customers", self.customers)):
            if path == f"/api/{kind}":
                if method == "POST":
                    item = {"id": len(store) + 1, **body}
                    store.append(item)
                    return httpx.Response(201, json=item)
                term = q.get("search", "").lower()
                return httpx.Response(200, json=[i for i in store if term in i["name"].lower()])
        if path == "/api/vente/sales" and method == "GET":
            return httpx.Response(200, json=[x for x in self.sales if not q.get("status") or x["status"] == q["status"]])
        if path == "/api/stock/status":
            return httpx.Response(200, json=[{"productId": 1, "productName": "Riz blanc 25 kg", "quantity": 14,
                                              "averageDailyOut": 7, "daysRemaining": 2, "risk": "CRITICAL"}])
        if path == "/api/stock/summary":
            return httpx.Response(200, json={"totalProducts": 2, "lowStockProducts": 1, "totalStockValue": 2550000})
        m = re.fullmatch(r"/api/stock/products/(\d+)/movements", path)
        if m:
            pid = int(m.group(1))
            if pid not in self.products:
                return err(404, "NOT_FOUND", f"Produit {pid} introuvable")
            if method == "GET":
                return httpx.Response(200, json=[])
            p = self.products[pid]
            if body["type"] == "OUT" and body["quantity"] > p["quantity"]:
                return err(422, "BUSINESS", f"Stock insuffisant pour {p['name']} : disponible {p['quantity']}")
            p["quantity"] += body["quantity"] if body["type"] == "IN" else -body["quantity"]
            return httpx.Response(201, json={"id": 99, "productId": pid, "type": body["type"],
                                             "quantity": body["quantity"], "reason": body.get("reason"),
                                             "stockAfter": p["quantity"], "createdAt": "2026-11-21T12:00:00Z"})
        if path == "/api/vente/sales/summary":
            return httpx.Response(200, json={"salesCount": 3, "totalRevenue": 310000, "averageBasket": 103333.33,
                                             "daily": [{"date": q.get("from"), "salesCount": 3, "revenue": 310000}]})
        if path == "/api/vente/sales/top-products":
            return httpx.Response(200, json=[{"productId": 2, "productName": "Huile 5 L", "totalQuantity": 8,
                                              "totalRevenue": 256000}])
        if path == "/api/vente/sales" and method == "POST":
            sale = {"id": len(self.sales) + 1, "customerId": body.get("customerId"), "status": "DRAFT",
                    "totalAmount": 0, "lines": body["lines"]}
            self.sales.append(sale)
            return httpx.Response(201, json=sale)
        m = re.fullmatch(r"/api/vente/sales/(\d+)/validate", path)
        if m:
            sale = self.sales[int(m.group(1)) - 1]
            for line in sale["lines"]:
                p = self.products[line["productId"]]
                if line["quantity"] > p["quantity"]:
                    return err(422, "BUSINESS", f"Stock insuffisant pour {p['name']}")
            for line in sale["lines"]:
                self.products[line["productId"]]["quantity"] -= line["quantity"]
            sale["status"] = "VALIDATED"
            return httpx.Response(200, json=sale)
        if path == "/api/rh/summary":
            return httpx.Response(200, json={"headcount": 12, "onLeaveToday": 1})
        return err(404, "NOT_FOUND", f"Route inconnue {method} {path}")
