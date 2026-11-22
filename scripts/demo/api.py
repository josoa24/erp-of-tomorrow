"""Petit client HTTP de la gateway (bibliothèque standard uniquement)."""
from __future__ import annotations

import json
import urllib.error
import urllib.request


class ApiError(RuntimeError):
    pass


class Api:
    def __init__(self, gateway: str, token: str | None = None) -> None:
        self.gateway = gateway.rstrip("/")
        self.token = token

    def request(self, method: str, path: str, body=None) -> tuple[int, object]:
        data = None if body is None else json.dumps(body).encode()
        req = urllib.request.Request(self.gateway + path, data=data, method=method)
        req.add_header("Content-Type", "application/json")
        if self.token:
            req.add_header("Authorization", f"Bearer {self.token}")
        try:
            with urllib.request.urlopen(req, timeout=60) as resp:
                return resp.status, _parse(resp.read())
        except urllib.error.HTTPError as err:
            return err.code, _parse(err.read())

    def must(self, method: str, path: str, body=None, expected=(200, 201, 204)) -> object:
        status, payload = self.request(method, path, body)
        if status not in expected:
            raise ApiError(f"{method} {path} -> {status} : {payload}")
        return payload

    def login(self, email: str, password: str) -> "Api":
        payload = Api(self.gateway).must("POST", "/api/auth/login", {"email": email, "password": password})
        return Api(self.gateway, payload["token"])


def _parse(raw: bytes):
    if not raw:
        return None
    try:
        return json.loads(raw)
    except ValueError:
        return raw.decode(errors="replace")
