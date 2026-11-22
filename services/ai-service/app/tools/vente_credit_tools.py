from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.erp.client import ErpClient, keep
from app.tools.spec import WRITE, Args, NoArgs, S, T, ToolSpec, decl, integer, string

METHODS = ["CASH", "MVOLA", "ORANGE_MONEY", "AIRTEL_MONEY"]


class CollectListArgs(Args):
    payment_status: Literal["TO_COLLECT", "OVERDUE", "PAID"] = "TO_COLLECT"


class PaymentArgs(Args):
    sale_id: int = Field(ge=1)
    amount: float = Field(gt=0)
    method: Literal["CASH", "MVOLA", "ORANGE_MONEY", "AIRTEL_MONEY"]
    reference: str | None = Field(default=None, max_length=64)


async def get_sales_kpis(erp: ErpClient, a: NoArgs):
    return await erp.call("GET", "/api/vente/sales/kpis")


async def get_credit_summary(erp: ErpClient, a: NoArgs):
    return await erp.call("GET", "/api/vente/sales/credit-summary")


async def list_sales_to_collect(erp: ErpClient, a: CollectListArgs):
    return keep(await erp.call("GET", "/api/vente/sales", params={"paymentStatus": a.payment_status}), 20, "createdAt")


async def record_sale_payment(erp: ErpClient, a: PaymentArgs):
    body = {"amount": a.amount, "method": a.method, "reference": a.reference}
    return await erp.call("POST", f"/api/vente/sales/{a.sale_id}/payments",
                          json={k: v for k, v in body.items() if v is not None})


SPECS = [
    ToolSpec(decl("get_sales_kpis", "Ventes : chiffre du jour, du mois, comparaison au mois précédent et au même jour "
                  "de la semaine dernière, encaissé du jour, panier moyen."), NoArgs, get_sales_kpis, "vente"),
    ToolSpec(decl("get_credit_summary", "Ventes à crédit : encours total, clients concernés, clients en retard."),
             NoArgs, get_credit_summary, "vente"),
    ToolSpec(decl("list_sales_to_collect", "Ventes à encaisser, en retard ou payées, avec échéance et reste dû.",
                  {"payment_status": string("Statut de paiement.", ["TO_COLLECT", "OVERDUE", "PAID"])}),
             CollectListArgs, list_sales_to_collect, "vente"),
    ToolSpec(decl("record_sale_payment", "Ventes : enregistre un encaissement sur une vente validée (non modifiable "
                  "ensuite)." + WRITE, {"sale_id": integer("Id de la vente."),
                                        "amount": S(type=T.NUMBER, description="Montant encaissé en Ar."),
                                        "method": string("Moyen de paiement.", METHODS),
                                        "reference": string("Référence de la transaction.")},
                  ["sale_id", "amount", "method"]), PaymentArgs, record_sale_payment, "vente",
             lambda a: f"Encaisser {a.amount:,.0f} Ar ({a.method}) sur la vente #{a.sale_id}.".replace(",", " ")),
]
