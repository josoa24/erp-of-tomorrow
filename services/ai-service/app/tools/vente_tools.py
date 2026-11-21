"""Outils du module Vente (rôles VENTE, ADMIN) -> /api/vente/... via la gateway."""
from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.erp import periods
from app.erp.client import ErpClient, keep
from app.tools.spec import (DATE_END, DATE_START, PERIOD, S, T, WRITE, Args, ToolSpec, decl, integer, string)


class PeriodArgs(Args):
    period: periods.Period = "all"
    start_date: str | None = Field(default=None, pattern=r"^\d{4}-\d{2}-\d{2}$")
    end_date: str | None = Field(default=None, pattern=r"^\d{4}-\d{2}-\d{2}$")


class SearchSalesArgs(PeriodArgs):
    customer_id: int | None = Field(default=None, ge=1)
    status: Literal["DRAFT", "VALIDATED"] | None = None


class TopArgs(Args):
    limit: int = Field(default=5, ge=1, le=50)


class CustomerSearchArgs(Args):
    search: str | None = Field(default=None, max_length=100)


class CustomerIdArgs(Args):
    customer_id: int = Field(ge=1)


class SaleLine(Args):
    product_id: int = Field(ge=1)
    quantity: int = Field(ge=1, le=100000)


class CreateSaleArgs(Args):
    customer_id: int | None = Field(default=None, ge=1)
    lines: list[SaleLine] = Field(min_length=1, max_length=50)
    validate_now: bool = True


class SaleIdArgs(Args):
    sale_id: int = Field(ge=1)


def _dates(a: PeriodArgs) -> dict | None:
    try:
        start, end = periods.resolve(a.period, a.start_date, a.end_date)
    except (periods.PeriodError, ValueError) as exc:
        return {"error": str(exc)}
    return {"from": start, "to": end}


def _bad_period(msg: str) -> dict:
    return {"success": False, "error": {"code": "VALIDATION", "message": msg}}


# ------------------------------- Lecture ------------------------------------ #
async def get_sales_summary(erp: ErpClient, a: PeriodArgs):
    d = _dates(a)
    if "error" in d:
        return _bad_period(d["error"])
    r = await erp.call("GET", "/api/vente/sales/summary", params=d)
    return {**r, "period": a.period, "from": d["from"], "to": d["to"]} if r.get("success") else r


async def get_top_products(erp: ErpClient, a: TopArgs):
    return await erp.call("GET", "/api/vente/sales/top-products", params={"limit": a.limit})


async def search_sales(erp: ErpClient, a: SearchSalesArgs):
    d = _dates(a)
    if "error" in d:
        return _bad_period(d["error"])
    return keep(await erp.call("GET", "/api/vente/sales", params={
        **d, "customerId": a.customer_id, "status": a.status}), 20, "createdAt")


async def search_customers(erp: ErpClient, a: CustomerSearchArgs):
    return keep(await erp.call("GET", "/api/vente/customers", params={"search": a.search}), 50)


async def get_customer_profile(erp: ErpClient, a: CustomerIdArgs):
    return await erp.call("GET", f"/api/vente/customers/{a.customer_id}/profile")


# ------------------------------ Écriture ------------------------------------ #
async def create_sale(erp: ErpClient, a: CreateSaleArgs):
    body = {"customerId": a.customer_id,
            "lines": [{"productId": l.product_id, "quantity": l.quantity} for l in a.lines]}
    draft = await erp.call("POST", "/api/vente/sales", json=body)
    if not draft.get("success") or not a.validate_now:
        return draft
    validated = await erp.call("POST", f"/api/vente/sales/{draft['id']}/validate")
    if validated.get("success"):
        return validated
    # Règle ERP : si la validation échoue, la vente reste en brouillon.
    return {**validated, "draft_sale_id": draft["id"],
            "note": f"La vente #{draft['id']} a été créée mais reste en brouillon (validation refusée)."}


async def validate_sale(erp: ErpClient, a: SaleIdArgs):
    return await erp.call("POST", f"/api/vente/sales/{a.sale_id}/validate")


def _sale_summary(a: CreateSaleArgs) -> str:
    lines = ", ".join(f"{l.quantity} x produit #{l.product_id}" for l in a.lines)
    who = f"client #{a.customer_id}" if a.customer_id else "sans client"
    then = " puis la valider (sortie de stock)" if a.validate_now else " en brouillon"
    return f"Créer une vente ({who}) : {lines}{then}."


_LINE = S(type=T.OBJECT, properties={"product_id": integer("Id produit (module Stock)."),
                                     "quantity": integer("Quantité.")}, required=["product_id", "quantity"])
_PERIOD = {"period": PERIOD, "start_date": DATE_START, "end_date": DATE_END}

SPECS = [
    ToolSpec(decl("get_sales_summary", "Chiffre d'affaires, nombre de ventes validées, panier moyen et détail par "
                  "jour sur une période. Pour comparer deux périodes, l'appeler une fois par période.", _PERIOD),
             PeriodArgs, get_sales_summary, "vente"),
    ToolSpec(decl("get_top_products", "Produits les plus vendus (quantité et CA), toutes périodes confondues.",
                  {"limit": integer("1-50, défaut 5.")}), TopArgs, get_top_products, "vente"),
    ToolSpec(decl("search_sales", "Liste des ventes (récentes d'abord) avec lignes et montant.",
                  {**_PERIOD, "customer_id": integer("Filtre client."),
                   "status": string("Statut.", ["DRAFT", "VALIDATED"])}), SearchSalesArgs, search_sales, "vente"),
    ToolSpec(decl("search_customers", "Clients (id, nom, téléphone, email, ville, type).",
                  {"search": string("Nom, téléphone, email ou ville.")}), CustomerSearchArgs, search_customers, "vente"),
    ToolSpec(decl("get_customer_profile", "Fiche 360 d'un client : total acheté, panier moyen, dernier achat, "
                  "produits préférés, dernières ventes.", {"customer_id": integer("Id client.")}, ["customer_id"]),
             CustomerIdArgs, get_customer_profile, "vente"),
    ToolSpec(decl("create_sale", "Crée une vente et la valide par défaut (sortie de stock). Le prix vient du "
                  "Stock." + WRITE, {"customer_id": integer("Id client (optionnel)."),
                                     "lines": S(type=T.ARRAY, items=_LINE),
                                     "validate_now": S(type=T.BOOLEAN, description="false = laisser en brouillon")},
                  ["lines"]), CreateSaleArgs, create_sale, "vente", _sale_summary),
    ToolSpec(decl("validate_sale", "Valide une vente en brouillon (sortie de stock, immuable)." + WRITE,
                  {"sale_id": integer("Id de la vente.")}, ["sale_id"]), SaleIdArgs, validate_sale, "vente",
             lambda a: f"Valider la vente #{a.sale_id} (sortie de stock, elle deviendra non modifiable)."),
]
