"""Comptes, stock, fournisseurs, bon de commande reçu, clients et ventes validées."""
from __future__ import annotations

from api import Api
from data import ACCOUNTS, CUSTOMERS, ORDER, PASSWORD, PRODUCTS, SALES, SUPPLIERS, TEAM_ACCOUNTS


def seed_accounts(admin: Api) -> dict[str, int]:
    for email, role in ACCOUNTS:
        status, _ = admin.request("POST", "/api/auth/users", {"email": email, "password": PASSWORD, "role": role})
        print(f"  compte {email} ({role}) : {'créé' if status == 201 else 'déjà présent' if status == 409 else status}")
    return {user["email"]: user["id"] for user in admin.must("GET", "/api/auth/users")}


def seed_team_accounts(admin: Api) -> None:
    for email, role, full_name, enabled, _ in TEAM_ACCOUNTS:
        status, user = admin.request("POST", "/api/auth/users",
                                     {"email": email, "password": PASSWORD, "role": role, "fullName": full_name})
        if status == 201 and not enabled:
            admin.must("POST", f"/api/auth/users/{user['id']}/disable")
        label = "créé" if status == 201 else "déjà présent" if status == 409 else status
        print(f"  compte {email} ({role}, {full_name}) : {label}")


def seed_products(admin: Api) -> dict[str, dict]:
    existing = {p["sku"]: p for p in admin.must("GET", "/api/stock/products")}
    for name, sku, unit, quantity, threshold, price in PRODUCTS:
        if sku not in existing:
            existing[sku] = admin.must("POST", "/api/stock/products", {
                "name": name, "sku": sku, "unit": unit, "quantity": quantity,
                "minThreshold": threshold, "unitPrice": price})
    print(f"  {len(existing)} produits")
    return existing


def seed_suppliers(admin: Api) -> dict[str, dict]:
    existing = {s["name"]: s for s in admin.must("GET", "/api/stock/suppliers")}
    for name, phone, delay, account in SUPPLIERS:
        if name not in existing:
            existing[name] = admin.must("POST", "/api/stock/suppliers", {
                "name": name, "phone": phone, "paymentTerms": "30 jours", "usualDeliveryDays": delay,
                "bankAccount": account})
    print(f"  {len(existing)} fournisseurs (avec compte de paiement de référence)")
    return existing


def seed_received_order(admin: Api, products: dict, suppliers: dict) -> dict:
    supplier = suppliers[ORDER["supplier"]]
    orders = admin.must("GET", f"/api/stock/purchase-orders?supplierId={supplier['id']}")
    if orders:
        return orders[0]
    order = admin.must("POST", "/api/stock/purchase-orders", {"supplierId": supplier["id"], "lines": [
        {"productId": products[ORDER["sku"]]["id"], "quantity": ORDER["quantity"], "unitPrice": ORDER["unitPrice"]}]})
    admin.must("POST", f"/api/stock/purchase-orders/{order['id']}/validate")
    order = admin.must("POST", f"/api/stock/purchase-orders/{order['id']}/receive")
    print(f"  bon de commande #{order['id']} validé et reçu ({ORDER['quantity']} x {ORDER['sku']})")
    return order


def seed_customers(admin: Api) -> list[dict]:
    existing = {c["name"]: c for c in admin.must("GET", "/api/vente/customers")}
    for name, kind, city in CUSTOMERS:
        if name not in existing:
            existing[name] = admin.must("POST", "/api/vente/customers", {"name": name, "type": kind, "city": city})
    return [existing[name] for name, _, _ in CUSTOMERS]


def seed_sales(admin: Api, products: dict, customers: list[dict]) -> None:
    if admin.must("GET", "/api/vente/sales?status=VALIDATED"):
        print("  ventes déjà présentes, rien à ajouter")
        return
    for sku, quantity, customer in SALES:
        sale = admin.must("POST", "/api/vente/sales", {"customerId": customers[customer]["id"],
                                                     "lines": [{"productId": products[sku]["id"], "quantity": quantity}]})
        admin.must("POST", f"/api/vente/sales/{sale['id']}/validate")
    print(f"  {len(SALES)} ventes validées (sorties de stock)")
