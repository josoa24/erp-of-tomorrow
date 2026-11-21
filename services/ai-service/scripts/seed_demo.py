"""Remplit l'ERP avec des données de démonstration, via l'API de la gateway (idempotent).

Usage :  python scripts/seed_demo.py [--gateway http://localhost:8080] [--email ...] [--password ...]
Crée : 2 comptes (stock / vente), 5 produits, 1 fournisseur, 2 clients et quelques ventes validées.
"""
from __future__ import annotations

import argparse
import os
from urllib.parse import quote

from erp_http import Api

PASSWORD = "demo12345"
USERS = [("stock@erp.local", "STOCK"), ("vente@erp.local", "VENTE")]
PRODUCTS = [  # name, sku, unit, quantity, minThreshold, unitPrice
    ("Riz blanc 25 kg", "RIZ-25", "sac", 40, 20, 45000),
    ("Huile 5 L", "HUI-5", "bidon", 60, 10, 32000),
    ("Sucre 1 kg", "SUC-1", "paquet", 120, 30, 3500),
    ("Savon de ménage", "SAV-1", "pièce", 12, 15, 1500),
    ("Farine 50 kg", "FAR-50", "sac", 25, 8, 95000),
]
CUSTOMERS = [("Épicerie du Marché", "ENTREPRISE", "Antananarivo"), ("Restaurant Le Baobab", "ENTREPRISE", "Toamasina")]
SALES = [("RIZ-25", 8, 0), ("HUI-5", 3, 0), ("RIZ-25", 7, 1), ("SUC-1", 20, 1), ("RIZ-25", 6, 0), ("FAR-50", 2, 1)]


def ensure_users(admin: Api) -> None:
    for email, role in USERS:
        status, _ = admin.request("POST", "/api/auth/users", {"email": email, "password": PASSWORD, "role": role})
        print(f"  compte {email} ({role}) : {'créé' if status == 201 else 'existe déjà' if status == 409 else status}")


def ensure_products(admin: Api) -> dict[str, dict]:
    _, existing = admin.request("GET", "/api/stock/products")
    by_sku = {p["sku"]: p for p in existing or []}
    for name, sku, unit, qty, threshold, price in PRODUCTS:
        if sku not in by_sku:
            status, p = admin.request("POST", "/api/stock/products", {"name": name, "sku": sku, "unit": unit,
                                      "quantity": qty, "minThreshold": threshold, "unitPrice": price})
            if status == 201:
                by_sku[sku] = p
            print(f"  produit {sku} : {'créé' if status == 201 else status}")
    return by_sku


def ensure_named(admin: Api, path: str, name: str, body: dict) -> dict:
    _, items = admin.request("GET", f"{path}?search={quote(name.split()[0])}")
    found = next((i for i in items or [] if i["name"] == name), None)
    if found:
        return found
    status, created = admin.request("POST", path, {"name": name, **body})
    print(f"  {path.split('/')[-1]} « {name} » : {'créé' if status == 201 else status}")
    return created or {}


def seed_sales(admin: Api, products: dict, customers: list[dict]) -> None:
    _, sales = admin.request("GET", "/api/vente/sales?status=VALIDATED")
    if sales:
        print(f"  ventes : {len(sales)} vente(s) validée(s) déjà présentes, rien à ajouter")
        return
    for sku, qty, cust in SALES:
        status, sale = admin.request("POST", "/api/vente/sales", {
            "customerId": customers[cust].get("id"), "lines": [{"productId": products[sku]["id"], "quantity": qty}]})
        if status == 201:
            status, _ = admin.request("POST", f"/api/vente/sales/{sale['id']}/validate")
        print(f"  vente {qty} x {sku} : {'validée' if status == 200 else status}")


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--gateway", default=os.getenv("GATEWAY_URL", "http://localhost:8080"))
    ap.add_argument("--email", default=os.getenv("ADMIN_EMAIL", "admin@erp.local"))
    ap.add_argument("--password", default=os.getenv("ADMIN_PASSWORD", "admin1234"))
    a = ap.parse_args()
    admin = Api(a.gateway).login(a.email, a.password)
    print("Données de démonstration :")
    ensure_users(admin)
    products = ensure_products(admin)
    supplier = ensure_named(admin, "/api/stock/suppliers", "Grossiste Analamanga",
                            {"phone": "0340000000", "paymentTerms": "30 jours", "usualDeliveryDays": 3})
    customers = [ensure_named(admin, "/api/vente/customers", n, {"type": t, "city": c}) for n, t, c in CUSTOMERS]
    seed_sales(admin, products, customers)
    print(f"Terminé. Fournisseur #{supplier.get('id')}, comptes de test : {', '.join(u for u, _ in USERS)} "
          f"(mot de passe {PASSWORD}).")


if __name__ == "__main__":
    main()
