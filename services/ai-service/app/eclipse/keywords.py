from __future__ import annotations

import re

from app.eclipse.text import any_of

STRONG = 2
WEAK = 1
MIN_SCORE = 2

ADAPTATION_KEYWORDS: dict[str, tuple[tuple[re.Pattern[str], int], ...]] = {
    "stock.lots-peremption": (
        (any_of(r"lots?\b"), STRONG), (any_of("perem", "perim"), STRONG), (any_of("expir"), STRONG),
        (any_of(r"dlc\b", r"dluo\b", r"date limite"), STRONG), (any_of("perissab"), STRONG),
    ),
    "ventes.mobile-money-credit": (
        (any_of(r"mobile money", "mvola", r"orange money", "airtel"), STRONG), (any_of("credit"), STRONG),
        (any_of(r"moyens? de paiement", r"mode de paiement"), STRONG),
        (any_of("paiement", r"pay(?:e|er|ent)\b", r"especes?\b"), WEAK), (any_of(r"ventes?\b"), WEAK),
    ),
    "relations.nif-plafond-credit": (
        (any_of(r"nif\b"), STRONG), (any_of(r"stat\b"), STRONG), (any_of("plafon"), STRONG),
        (any_of("encours", r"limite de credit"), STRONG),
        (any_of("professionnel", r"pros?\b", "entreprise"), WEAK), (any_of("credit"), WEAK),
        (any_of(r"clients?\b"), WEAK),
    ),
    "equipe.avances-salaire": (
        (any_of("avance", "acompte"), STRONG), (any_of("salair", r"paie\b"), WEAK), (any_of("plafon"), WEAK),
        (any_of(r"employes?\b", "salarie", "personnel"), WEAK),
    ),
}
