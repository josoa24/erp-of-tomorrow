"""Outils du module Stock (rôles STOCK, ADMIN) -> /api/stock/... via la gateway."""
from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.erp.client import ErpClient, keep
from app.tools.spec import (S, T, WRITE, Args, NoArgs, ToolSpec, decl, integer, string)

PID = integer("Identifiant numérique du produit (voir search_products).")


class SearchProductsArgs(Args):
    search: str | None = Field(default=None, max_length=100)
    low_stock: bool | None = None


class ProductIdArgs(Args):
    product_id: int = Field(ge=1)


class StatusArgs(Args):
    risk: Literal["OK", "WARNING", "CRITICAL"] | None = None


class SearchArgs(Args):
    search: str | None = Field(default=None, max_length=100)


class PurchaseOrdersArgs(Args):
    supplier_id: int | None = Field(default=None, ge=1)
    status: Literal["DRAFT", "VALIDATED", "RECEIVED"] | None = None


class MovementArgs(Args):
    product_id: int = Field(ge=1)
    type: Literal["IN", "OUT"]
    quantity: int = Field(ge=1, le=100000)
    reason: str | None = Field(default=None, max_length=200)


class OrderLine(Args):
    product_id: int = Field(ge=1)
    quantity: int = Field(ge=1, le=100000)
    unit_price: float = Field(ge=0)


class CreateOrderArgs(Args):
    supplier_id: int = Field(ge=1)
    lines: list[OrderLine] = Field(min_length=1, max_length=50)


class OrderIdArgs(Args):
    purchase_order_id: int = Field(ge=1)


# ------------------------------- Lecture ------------------------------------ #
async def search_products(erp: ErpClient, a: SearchProductsArgs):
    return keep(await erp.call("GET", "/api/stock/products",
                               params={"search": a.search, "lowStock": a.low_stock}), 50)


async def get_stock_status(erp: ErpClient, a: StatusArgs):
    return keep(await erp.call("GET", "/api/stock/status", params={"risk": a.risk}), 50)


async def get_product_autonomy(erp: ErpClient, a: ProductIdArgs):
    return await erp.call("GET", f"/api/stock/products/{a.product_id}/autonomy")


async def get_stock_summary(erp: ErpClient, a: NoArgs):
    return await erp.call("GET", "/api/stock/summary")


async def get_product_movements(erp: ErpClient, a: ProductIdArgs):
    return keep(await erp.call("GET", f"/api/stock/products/{a.product_id}/movements"), 20, "createdAt")


async def search_suppliers(erp: ErpClient, a: SearchArgs):
    return keep(await erp.call("GET", "/api/stock/suppliers", params={"search": a.search}), 50)


async def list_purchase_orders(erp: ErpClient, a: PurchaseOrdersArgs):
    return keep(await erp.call("GET", "/api/stock/purchase-orders",
                               params={"supplierId": a.supplier_id, "status": a.status}), 20, "createdAt")


# ------------------------------ Écriture ------------------------------------ #
async def record_stock_movement(erp: ErpClient, a: MovementArgs):
    return await erp.call("POST", f"/api/stock/products/{a.product_id}/movements",
                          json={"type": a.type, "quantity": a.quantity, "reason": a.reason})


async def create_purchase_order(erp: ErpClient, a: CreateOrderArgs):
    lines = [{"productId": l.product_id, "quantity": l.quantity, "unitPrice": l.unit_price} for l in a.lines]
    return await erp.call("POST", "/api/stock/purchase-orders", json={"supplierId": a.supplier_id, "lines": lines})


async def validate_purchase_order(erp: ErpClient, a: OrderIdArgs):
    return await erp.call("POST", f"/api/stock/purchase-orders/{a.purchase_order_id}/validate")


async def receive_purchase_order(erp: ErpClient, a: OrderIdArgs):
    return await erp.call("POST", f"/api/stock/purchase-orders/{a.purchase_order_id}/receive")


def _movement_summary(a: MovementArgs) -> str:
    kind = "Entrée de stock" if a.type == "IN" else "Sortie de stock"
    sign = "+" if a.type == "IN" else "-"
    return f"{kind} : {sign}{a.quantity} pour le produit #{a.product_id}" + (f" ({a.reason})" if a.reason else "") + "."


def _order_summary(a: CreateOrderArgs) -> str:
    lines = ", ".join(f"{l.quantity} x produit #{l.product_id} à {l.unit_price:g}" for l in a.lines)
    return f"Créer un bon de commande (brouillon) chez le fournisseur #{a.supplier_id} : {lines}."


_ORDER = {"purchase_order_id": integer("Identifiant du bon de commande.")}
_LINE = S(type=T.OBJECT, properties={"product_id": PID, "quantity": integer("Quantité."),
                                     "unit_price": S(type=T.NUMBER, description="Prix d'achat unitaire.")},
          required=["product_id", "quantity", "unit_price"])

SPECS = [
    ToolSpec(decl("search_products", "Produits (id, nom, SKU, quantité, seuil, prix, alerte). Filtres optionnels.",
                  {"search": string("Nom ou SKU."), "low_stock": S(type=T.BOOLEAN, description="true = en alerte")}),
             SearchProductsArgs, search_products, "stock"),
    ToolSpec(decl("get_stock_status", "État du stock avec autonomie (jours restants) et risque OK/WARNING/CRITICAL, "
                  "du plus urgent au moins urgent.", {"risk": string("Filtre de risque.", ["OK", "WARNING", "CRITICAL"])}),
             StatusArgs, get_stock_status, "stock"),
    ToolSpec(decl("get_product_autonomy", "Autonomie d'un produit : vente moyenne/jour (7 j), jours restants, risque.",
                  {"product_id": PID}, ["product_id"]), ProductIdArgs, get_product_autonomy, "stock"),
    ToolSpec(decl("get_stock_summary", "Synthèse : nombre de produits, nombre en alerte, valeur totale du stock."),
             NoArgs, get_stock_summary, "stock"),
    ToolSpec(decl("get_product_movements", "Derniers mouvements (entrées/sorties) d'un produit.",
                  {"product_id": PID}, ["product_id"]), ProductIdArgs, get_product_movements, "stock"),
    ToolSpec(decl("search_suppliers", "Fournisseurs (id, nom, contact, délai de livraison).",
                  {"search": string("Nom, téléphone ou email.")}), SearchArgs, search_suppliers, "stock"),
    ToolSpec(decl("list_purchase_orders", "Bons de commande fournisseurs (récents d'abord).",
                  {"supplier_id": integer("Filtre fournisseur."),
                   "status": string("Statut.", ["DRAFT", "VALIDATED", "RECEIVED"])}),
             PurchaseOrdersArgs, list_purchase_orders, "stock"),
    ToolSpec(decl("record_stock_movement", "Entrée (IN, réception) ou sortie (OUT, perte, casse) de stock." + WRITE,
                  {"product_id": PID, "type": string("IN ou OUT.", ["IN", "OUT"]),
                   "quantity": integer("Quantité."), "reason": string("Motif (fournisseur, bon, casse...).")},
                  ["product_id", "type", "quantity"]),
             MovementArgs, record_stock_movement, "stock", _movement_summary),
    ToolSpec(decl("create_purchase_order", "Crée un bon de commande fournisseur en brouillon." + WRITE,
                  {"supplier_id": integer("Identifiant du fournisseur."), "lines": S(type=T.ARRAY, items=_LINE)},
                  ["supplier_id", "lines"]),
             CreateOrderArgs, create_purchase_order, "stock", _order_summary),
    ToolSpec(decl("validate_purchase_order", "Valide un bon de commande brouillon (devient immuable)." + WRITE,
                  _ORDER, ["purchase_order_id"]), OrderIdArgs, validate_purchase_order, "stock",
             lambda a: f"Valider le bon de commande #{a.purchase_order_id} (il deviendra non modifiable)."),
    ToolSpec(decl("receive_purchase_order", "Réceptionne un bon validé : une entrée de stock par ligne." + WRITE,
                  _ORDER, ["purchase_order_id"]), OrderIdArgs, receive_purchase_order, "stock",
             lambda a: f"Réceptionner le bon de commande #{a.purchase_order_id} (entrées de stock pour chaque ligne)."),
]
