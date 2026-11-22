"""Outils de prévision de réapprovisionnement (rôles STOCK, ADMIN) -> /api/stock/.../replenishment.

Les quantités sont calculées par le stock-service (consommation sur 28 jours, stock de sécurité,
délai du fournisseur) : l'assistant les cite et les explique avec les hypothèses renvoyées, sans
jamais les recalculer.
"""
from __future__ import annotations

from typing import Any

from pydantic import Field

from app.erp.client import ErpClient
from app.tools.spec import S, T, Args, ToolSpec, decl, integer


class ForecastArgs(Args):
    product_id: int = Field(ge=1)


class ForecastListArgs(Args):
    only_needed: bool = True


async def get_replenishment_forecast(erp: ErpClient, a: ForecastArgs) -> dict[str, Any]:
    return await erp.call("GET", f"/api/stock/products/{a.product_id}/replenishment")


async def list_replenishment_needs(erp: ErpClient, a: ForecastListArgs) -> dict[str, Any]:
    return await erp.call("GET", "/api/stock/replenishment", params={"onlyNeeded": str(a.only_needed).lower()})


SPECS = [
    ToolSpec(decl("get_replenishment_forecast",
                  "Prévision de réapprovisionnement d'un produit : consommation moyenne (28 j), stock de sécurité, "
                  "point de commande, quantité recommandée, jours de couverture, fournisseur suggéré et hypothèses. "
                  "Si confidence = INSUFFICIENT_DATA, ne pas proposer de quantité.",
                  {"product_id": integer("Identifiant numérique du produit (voir search_products).")},
                  ["product_id"]), ForecastArgs, get_replenishment_forecast, "stock"),
    ToolSpec(decl("list_replenishment_needs",
                  "Produits à commander maintenant (les plus urgents d'abord), avec la quantité recommandée et le "
                  "fournisseur suggéré. only_needed=false pour voir tous les produits.",
                  {"only_needed": S(type=T.BOOLEAN, description="Seulement les produits à commander (défaut : oui).")}),
             ForecastListArgs, list_replenishment_needs, "stock"),
]
