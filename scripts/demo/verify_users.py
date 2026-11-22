"""Contrôles des comptes d'équipe créés par seed_demo.py (liste, désactivé, dernières connexions, connexion)."""
from __future__ import annotations

from api import Api
from checks import Checks
from data import PASSWORD, ROLE_ORDER, TEAM_ACCOUNTS


def verify_team_accounts(c: Checks, gateway: str, admin: Api) -> None:
    c.section("Comptes d'équipe (gestion des utilisateurs)")
    users = {u["email"]: u for u in admin.must("GET", "/api/auth/users")}
    c.expect("les comptes d'équipe existent", lambda: all(e in users for e, *_ in TEAM_ACCOUNTS))
    c.expect("rôles (liste) et noms complets conformes", lambda: all(
        users[e]["roles"] == r and users[e]["fullName"] == n for e, r, n, _, _ in TEAM_ACCOUNTS))
    c.expect("le rôle principal est le premier selon l'ordre de priorité", lambda: all(
        users[e]["role"] == min(r, key=ROLE_ORDER.index) for e, r, *_ in TEAM_ACCOUNTS))
    c.expect("au moins deux comptes d'équipe ont plusieurs rôles",
             lambda: sum(len(r) > 1 for _, r, *_ in TEAM_ACCOUNTS) >= 2)
    disabled = [e for e, _, _, enabled, _ in TEAM_ACCOUNTS if not enabled]
    c.expect("un seul compte d'équipe désactivé", lambda: [e for e, *_ in TEAM_ACCOUNTS if not users[e]["enabled"]] == disabled)
    c.expect("dernière connexion renseignée là où prévu", lambda: all(
        users[e].get("lastLoginAt") for e, _, _, _, days in TEAM_ACCOUNTS if days is not None))
    counts = c.status("compteurs des comptes", admin, "GET", "/api/auth/users/counts", 200)
    c.expect("compteurs cohérents avec la liste", lambda: counts["all"] == len(users)
             and counts["active"] == sum(u["enabled"] for u in users.values())
             and counts["disabled"] == sum(not u["enabled"] for u in users.values())
             and counts["admins"] == sum("ADMIN" in u["roles"] for u in users.values()), lambda: counts)
    active = next(e for e, _, _, enabled, _ in TEAM_ACCOUNTS if enabled)
    c.expect("un compte actif d'équipe se connecte", lambda: bool(Api(gateway).login(active, PASSWORD).token))
    multi, roles = next((e, r) for e, r, _, enabled, _ in TEAM_ACCOUNTS if enabled and len(r) > 1)
    login = Api(gateway).must("POST", "/api/auth/login", {"email": multi, "password": PASSWORD})
    c.expect("la réponse de connexion d'un compte multi-rôles porte la liste des rôles",
             lambda: login["roles"] == roles and login["role"] == roles[0], lambda: login)
    token = Api(gateway, login["token"])
    me = c.status("un compte multi-rôles lit son profil", token, "GET", "/api/auth/users/me", 200)
    c.expect("le profil porte les rôles", lambda: me["roles"] == roles, lambda: me)
    c.status("un compte multi-rôles n'accède pas à la gestion des comptes", token, "GET", "/api/auth/users", 403)
    c.status("un compte désactivé reçoit 401", Api(gateway), "POST", "/api/auth/login", 401,
             {"email": disabled[0], "password": PASSWORD})
