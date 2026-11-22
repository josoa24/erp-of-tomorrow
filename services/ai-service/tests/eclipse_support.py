from __future__ import annotations

from typing import Any

from fastapi.testclient import TestClient

from app.config import Settings
from app.main import create_app
from tests.fake_gemini import FakeClient
from tests.fake_modules_erp import FakeModulesErp
from tests.helpers import gateway_headers


def eclipse_client(erp: FakeModulesErp | None = None, script: list[Any] | None = None,
                   **settings: Any) -> tuple[TestClient, FakeClient | None, FakeModulesErp]:
    erp = erp or FakeModulesErp()
    fake = FakeClient(script) if script is not None else None
    values = {"gemini_api_key": "", "gemini_model": "t", "ai_audit_db": ":memory:", **settings}
    return TestClient(create_app(Settings(**values), client=fake, erp_http_client=erp.http())), fake, erp


def propose(client: TestClient, module_key: str, request: str, role: str = "ADMIN"):
    return client.post("/eclipse/adaptations/propose", json={"moduleKey": module_key, "request": request},
                       headers=gateway_headers(role))


def propose_module(client: TestClient, description: str, role: str = "ADMIN"):
    return client.post("/eclipse/modules/propose", json={"description": description},
                       headers=gateway_headers(role))


def last_audit(client: TestClient) -> dict[str, Any]:
    return client.get("/audit", headers=gateway_headers("ADMIN")).json()["items"][0]
