import asyncio
import json

from google.genai import types

from app.erp.client import ErpClient
from app.tools.registry import ToolRegistry
from tests.fake_erp import FakeErp
from tests.helpers import as_user


def reg(erp=None):
    return ToolRegistry(ErpClient((erp or FakeErp()).http()))


def run(c):
    return asyncio.run(c)


def test_tools_filtered_by_role_like_gateway():
    r = reg()
    names = lambda role: {d.name for d in r.gemini_tool(role).function_declarations}
    assert "search_products" in names("STOCK") and "get_sales_summary" not in names("STOCK")
    assert "get_sales_summary" in names("VENTE") and "search_products" not in names("VENTE")
    assert names("RH") == {"get_hr_summary", "search_employees", "confirm_action", "cancel_action"}
    assert names("ADMIN") == set(r.names)
    assert isinstance(r.gemini_tool("STOCK"), types.Tool) and r.gemini_tool("STOCK") is r.gemini_tool("STOCK")


def test_declarations_are_valid_and_json_serializable():
    r = reg()
    json.dumps(r.describe())
    writes = {d["name"] for d in r.describe() if d["requires_confirmation"]}
    assert writes == {"record_stock_movement", "create_purchase_order", "validate_purchase_order",
                      "receive_purchase_order", "create_sale", "validate_sale"}
    sale = next(d for d in r.describe() if d["name"] == "create_sale")
    assert sale["parameters"]["properties"]["lines"]["type"] == "ARRAY"


def test_read_tool_calls_gateway_with_user_token():
    erp = FakeErp()
    with as_user("STOCK"):
        out = run(reg(erp).execute("search_products", {"search": "riz", "low_stock": True}))
    assert out["success"] and out["count"] == 1 and out["data"][0]["sku"] == "RIZ-25"
    req = erp.requests[0]
    assert req.url.path == "/api/stock/products" and req.headers["authorization"] == "Bearer tok-STOCK"
    assert dict(req.url.params) == {"search": "riz", "lowStock": "true"}


def test_role_forbidden_before_any_call():
    erp = FakeErp()
    with as_user("VENTE"):
        out = run(reg(erp).execute("get_stock_summary", {}))
    assert out["error"]["code"] == "FORBIDDEN" and erp.requests == []


def test_unknown_tool_and_invalid_args():
    with as_user("ADMIN"):
        assert run(reg().execute("os.system", {}))["error"]["code"] == "UNKNOWN_TOOL"
        assert run(reg().execute("get_product_autonomy", {"product_id": 0}))["error"]["code"] == "VALIDATION"
        assert run(reg().execute("get_sales_summary", {"period": "demain"}))["error"]["code"] == "VALIDATION"


def test_no_user_means_unauthorized():
    out = run(reg().execute("get_stock_summary", {}))
    assert out["error"]["code"] == "FORBIDDEN"  # pas de rôle -> aucun module
