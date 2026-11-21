"""Petit client HTTP (bibliothèque standard uniquement) commun aux scripts de démo et de test."""
from __future__ import annotations

import json
import sys
import urllib.error
import urllib.request

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")  # console Windows


def _parse(raw: bytes):
    """JSON si possible, sinon le texte (ex. page HTML)."""
    if not raw:
        return None
    try:
        return json.loads(raw)
    except ValueError:
        return {"message": raw.decode(errors="replace")[:200]}


class Api:
    def __init__(self, gateway: str, token: str | None = None) -> None:
        self.gateway = gateway.rstrip("/")
        self.token = token

    def request(self, method: str, path: str, body=None, timeout: float = 60):
        """Renvoie (statut HTTP, corps JSON ou None)."""
        headers = {"Content-Type": "application/json"}
        if self.token:
            headers["Authorization"] = f"Bearer {self.token}"
        data = json.dumps(body).encode() if body is not None else None
        req = urllib.request.Request(self.gateway + path, data=data, headers=headers, method=method)
        try:
            with urllib.request.urlopen(req, timeout=timeout) as r:
                return r.status, _parse(r.read())
        except urllib.error.HTTPError as e:
            return e.code, _parse(e.read())

    def login(self, email: str, password: str) -> "Api":
        status, body = self.request("POST", "/api/auth/login", {"email": email, "password": password})
        if status != 200:
            raise SystemExit(f"Connexion impossible pour {email} : HTTP {status} {body}")
        return Api(self.gateway, body["token"])

    def stream(self, path: str, body: dict, timeout: float = 120) -> list[tuple[str, dict]]:
        """POST en Server-Sent Events : renvoie la liste (événement, données)."""
        req = urllib.request.Request(self.gateway + path, data=json.dumps(body).encode(), method="POST",
                                     headers={"Content-Type": "application/json",
                                              "Authorization": f"Bearer {self.token}"})
        events, event, data = [], "message", ""
        try:
            with urllib.request.urlopen(req, timeout=timeout) as r:
                for raw in r:
                    line = raw.decode("utf-8").rstrip("\n").rstrip("\r")
                    if line.startswith("event:"):
                        event = line[6:].strip()
                    elif line.startswith("data:"):
                        data += line[5:].strip()
                    elif not line and data:
                        events.append((event, json.loads(data)))
                        event, data = "message", ""
        except urllib.error.HTTPError as e:
            events.append(("error", {"status": e.code, "message": e.read().decode(errors="replace")[:300]}))
        return events
