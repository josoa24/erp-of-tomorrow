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

ADD_VERBS = any_of(r"ajout\w*", r"rajout\w*", r"activ(?:e|er|ez)\b", r"mets?\b", r"mettre\b", r"integr\w*")
QUESTION_WORDS = any_of(
    r"combien\b", r"quel(?:le)?s?\b", r"qui\b", r"quand\b", r"pourquoi\b", r"comment\b", r"liste[rz]?\b",
    r"montre[rz]?\b", r"affiche[rz]?\b", r"donne[rz]?\b", r"cherche[rz]?\b", r"trouve[rz]?\b",
    r"est ce qu", r"y a t il\b", r"qu est ce\b", r"savoir\b", r"connaitre\b")
EXPLICIT_CREATE = re.compile(
    r"\b(?:cree[rz]?|creation|nouveau|nouvel|ajoute[rz]?|fais|faire|construi\w*)\b(?:\s+\w+){0,3}?\s+modules?\b")
SOFT_CREATE = any_of(
    r"(?:je veux|je voudrais|j aimerais|je souhaite|on veut|il faut) (?:suivre|gerer)\b",
    r"(?:suivre|gerer) (?:mes|nos)\b")
