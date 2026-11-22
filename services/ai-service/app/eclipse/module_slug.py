from __future__ import annotations

import re
import unicodedata

BUILT_IN_MODULES = frozenset({"stock", "ventes", "relations", "equipe"})
MAX_SLUG_LENGTH = 60


def slug_from_name(name: str) -> str:
    ascii_name = "".join(c for c in unicodedata.normalize("NFD", name.strip()) if not unicodedata.combining(c))
    slug = re.sub(r"[^a-z0-9]+", "-", ascii_name.lower()).strip("-")
    return slug[:MAX_SLUG_LENGTH].rstrip("-")


def check_module_name(name: str) -> None:
    slug = slug_from_name(name)
    if not slug:
        raise ValueError("le nom du module doit contenir au moins une lettre ou un chiffre")
    if slug in BUILT_IN_MODULES:
        raise ValueError(f"le nom « {name} » est réservé à un module de base")
