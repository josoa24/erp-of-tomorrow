"""Utilitaires de test : identité gateway, utilisateur courant, application complète."""
from __future__ import annotations

from contextlib import contextmanager

from fastapi.testclient import TestClient

from app.config import Settings
from app.erp.context import UserContext, current_user
from app.main import create_app
from tests.fake_erp import FakeErp
from tests.fake_gemini import FakeClient


def gateway_headers(role: str = "ADMIN", user_id: str = "1") -> dict[str, str]:
    """En-têtes ajoutés par la gateway après vérification du JWT."""
    return {"Authorization": f"Bearer tok-{role}", "X-User-Id": user_id,
            "X-User-Email": f"{role.lower()}@test.local", "X-User-Role": role}


@contextmanager
def as_user(role: str = "ADMIN", user_id: str = "1"):
    token = current_user.set(UserContext(token=f"tok-{role}", user_id=user_id, email="t@t", role=role))
    try:
        yield
    finally:
        current_user.reset(token)


def make_app(script, erp: FakeErp | None = None, **settings) -> tuple[TestClient, FakeClient, FakeErp]:
    erp = erp or FakeErp()
    fake = FakeClient(script)
    app = create_app(Settings(gemini_model="t", **settings), client=fake, erp_http_client=erp.http())
    return TestClient(app), fake, erp
