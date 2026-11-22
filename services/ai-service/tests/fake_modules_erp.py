from __future__ import annotations

import httpx

from tests.fake_erp import err

ADAPTATIONS = "/api/modules/adaptations"
DEFINITIONS = "/api/modules/definitions/"


class FakeModulesErp:
    def __init__(self, active: tuple[str, ...] = (), names: dict[str, str] | None = None,
                 down: bool = False) -> None:
        self.active = list(active)
        self.names = names or {}
        self.down = down
        self.requests: list[httpx.Request] = []

    def http(self) -> httpx.AsyncClient:
        return httpx.AsyncClient(base_url="http://gateway.test", transport=httpx.MockTransport(self.handle))

    def paths(self) -> list[str]:
        return [request.url.path for request in self.requests]

    def activation(self, key: str) -> dict[str, str]:
        return {"moduleKey": key.split(".")[0], "adaptationKey": key, "activatedBy": "1",
                "activatedAt": "2026-11-22T08:00:00Z"}

    def handle(self, request: httpx.Request) -> httpx.Response:
        self.requests.append(request)
        if self.down:
            raise httpx.ConnectError("module-service arrêté", request=request)
        if not request.headers.get("authorization", "").startswith("Bearer tok-"):
            return err(401, "UNAUTHORIZED", "Authentification requise")
        path = request.url.path
        if path == ADAPTATIONS:
            return httpx.Response(200, json=[self.activation(key) for key in self.active])
        slug = path.removeprefix(DEFINITIONS)
        if path.startswith(DEFINITIONS) and slug in self.names:
            return httpx.Response(200, json={"id": 1, "slug": slug, "name": self.names[slug], "icon": "truck"})
        return err(404, "NOT_FOUND", "Module introuvable")
