"""Outils de prévision : rôles, appel de la gateway avec le token, étiquette ESTIMATION."""
import asyncio

import httpx

from app.agent.records import ToolCallRecord
from app.erp.client import ErpClient
from app.tools.registry import ToolRegistry
from app.trust.assessment import assess
from tests.helpers import as_user

FORECAST = {"productId": 1, "productName": "Riz blanc 25 kg", "currentStock": 10, "reorderPoint": 14.0,
            "recommendedQuantity": 94, "needsReorder": True, "confidence": "OK", "assumptions": ["28 jours"]}


class Gateway:
    def __init__(self) -> None:
        self.requests: list[httpx.Request] = []

    def http(self) -> httpx.AsyncClient:
        return httpx.AsyncClient(base_url="http://gateway.test", transport=httpx.MockTransport(self.handle))

    def handle(self, request: httpx.Request) -> httpx.Response:
        self.requests.append(request)
        if request.url.path == "/api/stock/products/1/replenishment":
            return httpx.Response(200, json=FORECAST)
        if request.url.path == "/api/stock/replenishment":
            return httpx.Response(200, json=[FORECAST])
        return httpx.Response(404, json={"status": 404, "type": "NOT_FOUND", "message": "inconnu"})


def run(tool, args, role="STOCK"):
    gateway = Gateway()
    with as_user(role):
        out = asyncio.run(ToolRegistry(ErpClient(gateway.http())).execute(tool, args))
    return out, gateway.requests


def test_forecast_tools_only_for_stock_and_admin():
    names = lambda role: {d["name"] for d in ToolRegistry(None).describe(role)}
    for role in ("STOCK", "ADMIN"):
        assert {"get_replenishment_forecast", "list_replenishment_needs"} <= names(role)
    for role in ("VENTE", "RH", "CRM"):
        assert not {"get_replenishment_forecast", "list_replenishment_needs"} & names(role)


def test_forecast_calls_stock_service_with_user_token():
    out, requests = run("get_replenishment_forecast", {"product_id": 1})
    assert out["recommendedQuantity"] == 94 and out["success"] is True
    assert requests[0].url.path == "/api/stock/products/1/replenishment"
    assert requests[0].headers["authorization"] == "Bearer tok-STOCK"


def test_needs_list_sends_only_needed_flag():
    _, requests = run("list_replenishment_needs", {"only_needed": False}, role="ADMIN")
    assert requests[0].url.params["onlyNeeded"] == "false"


def test_invalid_product_id_is_refused_before_any_call():
    out, requests = run("get_replenishment_forecast", {"product_id": 0})
    assert out["success"] is False and requests == []


def test_forecast_answer_is_labelled_estimation_with_verified_quantity():
    record = ToolCallRecord(name="get_replenishment_forecast", arguments={}, success=True, duration_ms=1, turn=1,
                            result=FORECAST)
    trust = assess("Combien de riz commander ?", "Il faut commander 94 sacs de riz.", [record])
    assert trust["label"] == "ESTIMATION" and "94" in trust["verifiedNumbers"]
