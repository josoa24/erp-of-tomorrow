from __future__ import annotations

from typing import Iterable

from app.erp.context import normalize_roles

HOME_MODULE = "accueil"
BASE_MODULE_LABELS: dict[str, str] = {"ventes": "Ventes", "stock": "Stock", "equipe": "Équipe",
                                      "relations": "Relations"}
BASE_MODULE_ROLES: dict[str, str] = {"ventes": "VENTE", "stock": "STOCK", "equipe": "RH", "relations": "CRM"}


def is_base_module(module_key: str) -> bool:
    return module_key in BASE_MODULE_LABELS


def can_access(module_key: str, roles: str | Iterable[str]) -> bool:
    held = normalize_roles(roles)
    if "ADMIN" in held or not is_base_module(module_key):
        return True
    return BASE_MODULE_ROLES[module_key] in held


def accessible_base_modules(roles: str | Iterable[str]) -> list[str]:
    return [key for key in BASE_MODULE_LABELS if can_access(key, roles)]


def slug_label(slug: str) -> str:
    words = slug.replace("-", " ").strip()
    return words[:1].upper() + words[1:]
