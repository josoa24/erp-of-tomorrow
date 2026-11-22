"""Contrôles transverses : authentification, rôles, CORS, format des erreurs."""
from __future__ import annotations

import urllib.request

from api import Api
from checks import Checks
from data import PASSWORD


def verify_platform(c: Checks, gateway: str, admin: Api) -> dict[str, Api]:
    c.section("Authentification et rôles (gateway + auth-service)")
    anonymous = Api(gateway)
    c.status("connexion refusée avec un mauvais mot de passe", anonymous, "POST", "/api/auth/login", 401,
             {"email": "admin@erp.local", "password": "mauvais"})
    c.status("appel sans token refusé", anonymous, "GET", "/api/stock/products", 401)
    users = {role: Api(gateway).login(f"{role.lower()}@erp.local", PASSWORD)
             for role in ("STOCK", "VENTE", "RH", "CRM")}
    c.expect("les 4 comptes métier se connectent", lambda: all(u.token for u in users.values()))
    c.status("VENTE ne lit pas le stock", users["VENTE"], "GET", "/api/stock/products", 403)
    c.status("STOCK ne lit pas les RH", users["STOCK"], "GET", "/api/rh/employees", 403)
    c.status("RH ne gère pas les comptes", users["RH"], "GET", "/api/auth/users", 403)
    c.status("STOCK lit son module", users["STOCK"], "GET", "/api/stock/products", 200)
    c.status("ADMIN gère les comptes", admin, "GET", "/api/auth/users", 200)

    c.section("Erreurs et CORS")
    error = c.status("adresse inconnue", admin, "GET", "/api/stock/inexistant", 404)
    c.expect("erreur au format commun {status, type, message}",
             lambda: isinstance(error, dict) and error.get("type") == "NOT_FOUND" and "message" in error, lambda: error)
    invalid = c.status("corps invalide", admin, "POST", "/api/stock/products", 400, {"name": ""})
    c.expect("type VALIDATION", lambda: invalid.get("type") == "VALIDATION", lambda: invalid)
    c.expect("préflight CORS accepté pour une origine locale", lambda: _preflight(gateway) == "http://localhost:5173")
    return users


def _preflight(gateway: str) -> str | None:
    req = urllib.request.Request(gateway + "/api/stock/products", method="OPTIONS")
    req.add_header("Origin", "http://localhost:5173")
    req.add_header("Access-Control-Request-Method", "GET")
    with urllib.request.urlopen(req, timeout=10) as resp:
        return resp.headers.get("Access-Control-Allow-Origin")
