"""Client HTTP vers la gateway de l'ERP, avec le token de l'utilisateur.

Chaque appel passe par la gateway (http://gateway/api/<module>/...) : les droits du rôle
sont donc appliqués par l'ERP lui-même (401/403 si non autorisé).
Réponses normalisées pour l'agent :
    succès objet : {"success": True, ...}
    succès liste : {"success": True, "count": n, "data": [...]}
    erreur ERP   : {"success": False, "error": {"code": <type ERP>, "status": 4xx, "message": ...}}
"""
from __future__ import annotations

import logging
from typing import Any

import httpx

from app.erp.context import current_user

logger = logging.getLogger(__name__)


def _error(code: str, message: str, status: int | None = None) -> dict[str, Any]:
    err: dict[str, Any] = {"code": code, "message": message}
    if status is not None:
        err["status"] = status
    return {"success": False, "error": err}


class ErpClient:
    def __init__(self, client: httpx.AsyncClient) -> None:
        self._client = client  # base_url = gateway, connexions réutilisées

    @staticmethod
    def create_http(base_url: str, timeout_s: float) -> httpx.AsyncClient:
        return httpx.AsyncClient(base_url=base_url.rstrip("/"), timeout=httpx.Timeout(timeout_s),
                                 headers={"Accept": "application/json"})

    async def call(self, method: str, path: str, *, params: dict | None = None,
                   json: Any = None, token: str | None = None) -> dict[str, Any]:
        user = current_user.get()
        token = token or (user.token if user else None)
        if not token:
            return _error("UNAUTHORIZED", "Aucun utilisateur authentifié pour cet appel.")
        clean = {k: v for k, v in (params or {}).items() if v is not None}
        try:
            resp = await self._client.request(method, path, params=clean, json=json,
                                              headers={"Authorization": f"Bearer {token}"})
        except httpx.TimeoutException:
            return _error("SERVER", "L'ERP n'a pas répondu à temps.")
        except httpx.HTTPError as exc:
            logger.warning("Gateway ERP injoignable : %s", type(exc).__name__)
            return _error("SERVER", "La gateway de l'ERP est injoignable.")
        return self._normalize(resp)

    @staticmethod
    def _normalize(resp: httpx.Response) -> dict[str, Any]:
        try:
            body = resp.json() if resp.content else None
        except ValueError:
            body = None
        if resp.is_success:
            if isinstance(body, list):
                return {"success": True, "count": len(body), "data": body}
            if isinstance(body, dict):
                return {"success": True, **body}
            return {"success": True}
        if isinstance(body, dict) and "type" in body:  # format d'erreur commun de l'ERP
            return _error(body["type"], body.get("message", ""), resp.status_code)
        return _error("SERVER", f"Réponse inattendue de l'ERP (HTTP {resp.status_code}).", resp.status_code)


def keep(result: dict[str, Any], limit: int, newest_first_key: str | None = None) -> dict[str, Any]:
    """Limite une liste renvoyée à l'IA (économie de tokens), éventuellement triée du plus récent."""
    data = result.get("data")
    if not result.get("success") or not isinstance(data, list):
        return result
    if newest_first_key:
        data = sorted(data, key=lambda x: str(x.get(newest_first_key) or ""), reverse=True)
    if len(data) > limit:
        return {**result, "data": data[:limit], "truncated": True, "shown": limit}
    return {**result, "data": data}
