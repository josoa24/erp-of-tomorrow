import asyncio
from datetime import date

import httpx
import pytest

from app.erp import periods
from app.erp.client import ErpClient, keep
from app.tools.registry import ToolRegistry
from tests.fake_erp import FakeErp
from tests.helpers import as_user


def run(c):
    return asyncio.run(c)


def test_normalize_list_object_and_erp_error():
    erp = ErpClient(FakeErp().http())
    with as_user("ADMIN"):
        assert run(erp.call("GET", "/api/stock/summary"))["totalProducts"] == 2
        lst = run(erp.call("GET", "/api/stock/products"))
        assert lst["count"] == 2 and isinstance(lst["data"], list)
        e = run(erp.call("POST", "/api/stock/products/9/movements", json={"type": "IN", "quantity": 1}))
        assert e == {"success": False, "error": {"code": "NOT_FOUND", "message": "Produit 9 introuvable", "status": 404}}
    with as_user("STOCK"):
        assert run(erp.call("GET", "/api/vente/sales/summary"))["error"]["code"] == "FORBIDDEN"


def test_gateway_down():
    def boom(request):
        raise httpx.ConnectError("refused", request=request)
    erp = ErpClient(httpx.AsyncClient(base_url="http://x", transport=httpx.MockTransport(boom)))
    with as_user("ADMIN"):
        assert run(erp.call("GET", "/api/stock/summary"))["error"]["code"] == "SERVER"


def test_keep_truncates_newest_first():
    r = {"success": True, "count": 30, "data": [{"createdAt": f"2026-11-{d:02d}"} for d in range(1, 31)]}
    k = keep(r, 5, "createdAt")
    assert k["truncated"] and k["data"][0]["createdAt"] == "2026-11-30" and len(k["data"]) == 5


def test_periods():
    t = date(2026, 11, 21)
    assert periods.resolve("today", today=t) == ("2026-11-21", "2026-11-21")
    assert periods.resolve("last_week", today=t) == ("2026-11-09", "2026-11-15")
    assert periods.resolve("last_month", today=t) == ("2026-10-01", "2026-10-31")
    assert periods.resolve("all", today=t) == (None, None)
    with pytest.raises(periods.PeriodError):
        periods.resolve("custom", "2026-11-10", "2026-11-01")


def test_sales_summary_sends_dates():
    erp = FakeErp()
    with as_user("VENTE"):
        out = run(ToolRegistry(ErpClient(erp.http())).execute("get_sales_summary", {"period": "today"}))
    assert out["success"] and out["totalRevenue"] == 310000 and out["from"] == out["to"]
    assert set(dict(erp.requests[0].url.params)) == {"from", "to"}
