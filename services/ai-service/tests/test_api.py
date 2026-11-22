from tests.fake_gemini import fc_response, text_response
from tests.helpers import gateway_headers, make_app


def test_health_ui_and_openapi_path():
    c, _, _ = make_app([])
    with c:
        assert c.get("/health").json() == {"status": "ok", "service": "ai-service"}
        assert "/api/auth/login" in c.get("/ui").text
        assert c.get("/v3/api-docs").json()["info"]["title"] == "AI"   # lu par le Swagger de la gateway


def test_requires_gateway_identity():
    c, _, _ = make_app([])
    with c:
        r = c.post("/agent/chat", json={"message": "x"})
        assert r.status_code == 401 and r.json()["type"] == "UNAUTHORIZED" and "timestamp" in r.json()
        assert c.get("/tools").status_code == 401


def test_tools_for_role():
    c, _, _ = make_app([])
    with c:
        t = c.get("/tools", headers=gateway_headers("VENTE")).json()
        assert t["role"] == "VENTE" and {x["module"] for x in t["tools"]} == {"vente", "system"}


def test_chat_json():
    c, _, erp = make_app([fc_response(("get_sales_summary", {"period": "this_month"})),
                          text_response("CA du mois : 310 000.")])
    with c:
        r = c.post("/agent/chat", json={"message": "CA du mois ?"}, headers=gateway_headers("VENTE"))
        body = r.json()
        assert r.status_code == 200 and body["response"] == "CA du mois : 310 000."
        assert body["tool_calls"][0]["name"] == "get_sales_summary" and body["history_turns"] == 1
        assert erp.requests[0].headers["authorization"] == "Bearer tok-VENTE"


def test_validation_error_format():
    c, _, _ = make_app([])
    with c:
        r = c.post("/agent/chat", json={"message": ""}, headers=gateway_headers())
        assert r.status_code == 400 and r.json()["type"] == "VALIDATION"


def test_gemini_timeout_format():
    c, _, _ = make_app([TimeoutError()])
    with c:
        r = c.post("/agent/chat", json={"message": "x"}, headers=gateway_headers())
        assert r.status_code == 504 and r.json()["type"] == "SERVER"


def test_no_key_returns_503():
    from fastapi.testclient import TestClient
    from app.config import Settings
    from app.main import create_app
    from tests.fake_erp import FakeErp
    with TestClient(create_app(Settings(gemini_api_key="", ai_audit_db=":memory:"), erp_http_client=FakeErp().http())) as c:
        assert c.post("/agent/chat", json={"message": "x"}, headers=gateway_headers()).status_code == 503
