"""RH, CRM et factures fournisseurs (une saine, une piégée) pour Invoice Shield."""
from __future__ import annotations

from datetime import date, timedelta

from api import Api
from data import DEPARTMENTS, EMPLOYEES, INJECTION, INTERACTIONS, ORDER, SUPPLIERS


def seed_hr(admin: Api, user_ids: dict[str, int]) -> None:
    departments = {d["name"]: d for d in admin.must("GET", "/api/rh/departments")}
    for name in DEPARTMENTS:
        if name not in departments:
            departments[name] = admin.must("POST", "/api/rh/departments", {"name": name})
    employees = {e["email"]: e for e in admin.must("GET", "/api/rh/employees")}
    for first, last, position, dept, salary, account in EMPLOYEES:
        email = f"{first.lower()}.{last.lower()}@pme.mg"
        if email not in employees:
            employees[email] = admin.must("POST", "/api/rh/employees", {
                "firstName": first, "lastName": last, "email": email, "position": position,
                "departmentId": departments[dept]["id"], "hireDate": "2024-03-01", "baseSalary": salary,
                "userId": user_ids.get(account) if account else None})
    driver = employees["fara.ravelo@pme.mg"]
    if not admin.must("GET", f"/api/rh/leaves?employeeId={driver['id']}"):
        start = date.today() + timedelta(days=7)
        admin.must("POST", "/api/rh/leaves", {"employeeId": driver["id"], "startDate": str(start),
                                             "endDate": str(start + timedelta(days=2)), "reason": "Congé familial"})
    admin.request("POST", "/api/rh/attendances/check-in", {"employeeId": employees["hery.rakoto@pme.mg"]["id"]})
    print(f"  {len(departments)} départements, {len(employees)} employés, 1 congé en attente, 1 pointage")


def seed_crm(admin: Api, customers: list[dict], suppliers: dict) -> None:
    ids = {("CUSTOMER", c["name"]): c["id"] for c in customers}
    ids.update({("SUPPLIER", name): s["id"] for name, s in suppliers.items()})
    for kind, name, channel, summary in INTERACTIONS:
        path = f"/api/crm/parties/{kind}/{ids[(kind, name)]}/interactions"
        if not admin.must("GET", path):
            admin.must("POST", path, {"type": channel, "summary": summary})
    print(f"  {len(INTERACTIONS)} échanges CRM")


def seed_invoices(stock: Api, suppliers: dict, products: dict, order: dict) -> None:
    if stock.must("GET", "/api/stock/supplier-invoices"):
        print("  factures déjà présentes, rien à ajouter")
        return
    supplier = suppliers[ORDER["supplier"]]
    account = next(a for n, _, _, a in SUPPLIERS if n == ORDER["supplier"])
    product_id = products[ORDER["sku"]]["id"]
    clean = _invoice(supplier["id"], product_id, "FA-2026-0418", order["id"], account, ORDER["quantity"], None)
    trapped = _invoice(supplier["id"], product_id, "FA-2026-0419", order["id"],
                       "MG46 9999 0000 1111 2222 3333 999", 20, INJECTION)
    trapped["totalAmount"] = trapped["totalAmount"] + 500000
    for label, body in (("saine", clean), ("piégée", trapped)):
        invoice = stock.must("POST", "/api/stock/supplier-invoices", body)
        codes = ", ".join(sorted({a["code"] for a in invoice["alerts"]})) or "aucune alerte"
        print(f"  facture {label} {body['invoiceNumber']} : {invoice['status']} ({codes})")


def _invoice(supplier_id: int, product_id: int, number: str, order_id: int, account: str, quantity: int,
             notes) -> dict:
    subtotal = quantity * ORDER["unitPrice"]
    tax = subtotal // 5
    return {"supplierId": supplier_id, "invoiceNumber": number, "invoiceDate": str(date.today()),
            "purchaseOrderId": order_id, "bankAccount": account, "subtotal": subtotal, "taxAmount": tax,
            "totalAmount": subtotal + tax, "notes": notes,
            "lines": [{"productId": product_id, "description": "Riz blanc 25 kg", "quantity": quantity,
                       "unitPrice": ORDER["unitPrice"]}]}
