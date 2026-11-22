"""Extraction des nombres cités dans un texte et des valeurs connues d'un résultat JSON.

Formats reconnus : « 1 440 000 » (espace, espace insécable ou fine), « 1,440,000 », « 1.440.000 »,
« 12,5 », « 12.5 », « 1 234,56 », « 1,234.56 ». Un séparateur unique suivi d'exactement 3 chiffres
(« 1,440 ») est ambigu : les deux lectures (1440 et 1,44) sont essayées.
Ignorés : les marqueurs de liste en début de ligne (« 1. », « 2) ») et les entiers triviaux 0 et 1.
Tolérance : un nombre arrondi est accepté à une demi-unité près de sa dernière décimale affichée
(« 103 333 » vérifie 103333.33, « 12,5 » vérifie 12.48).
"""
from __future__ import annotations

import re
from dataclasses import dataclass
from decimal import Decimal, InvalidOperation
from typing import Any

_NUMBER = re.compile(r"(?<![\w.,])(?:\d{1,3}(?:[   ]\d{3})+(?:[.,]\d+)?|\d+(?:[.,]\d+)*)(?!\d)")
_LIST_MARKER = re.compile(r"(?m)^\s*\d+[.)]\s+")
_SPACES = re.compile(r"[   ]")
TRIVIAL = {Decimal(0), Decimal(1)}


@dataclass(frozen=True)
class Cited:
    raw: str
    readings: tuple[tuple[Decimal, Decimal], ...]  # (valeur, pas de la dernière décimale)

    def trivial(self) -> bool:
        return all(v in TRIVIAL and step == 1 for v, step in self.readings)

    def matches(self, known: set[Decimal]) -> bool:
        return any(abs(k - v) <= step / 2 for v, step in self.readings for k in known)


def _reading(integer: str, decimals: str = "") -> tuple[Decimal, Decimal]:
    value = Decimal(f"{integer}.{decimals}" if decimals else integer)
    return value, Decimal(1).scaleb(-len(decimals))


def _readings(raw: str) -> tuple[tuple[Decimal, Decimal], ...]:
    s = _SPACES.sub("", raw)
    if "," in s and "." in s:
        dec = "," if s.rindex(",") > s.rindex(".") else "."
        integer, decimals = s.rsplit(dec, 1)
        return (_reading(re.sub(r"[.,]", "", integer), decimals),)
    sep = "," if "," in s else "." if "." in s else None
    if sep is None:
        return (_reading(s),)
    parts = s.split(sep)
    if len(parts) > 2:
        return (_reading("".join(parts)),)
    if len(parts[1]) == 3 and len(parts[0]) <= 3:
        return (_reading("".join(parts)), _reading(parts[0], parts[1]))
    return (_reading(parts[0], parts[1]),)


def extract_numbers(text: str) -> list[Cited]:
    """Nombres non triviaux cités dans le texte, sans doublon, dans l'ordre d'apparition."""
    seen: dict[str, Cited] = {}
    for match in _NUMBER.finditer(_LIST_MARKER.sub(" ", text or "")):
        raw = match.group(0)
        try:
            cited = Cited(raw, _readings(raw))
        except (InvalidOperation, ValueError):
            continue
        if not cited.trivial() and raw not in seen:
            seen[raw] = cited
    return list(seen.values())


def known_values(obj: Any, into: set[Decimal] | None = None) -> set[Decimal]:
    """Toutes les valeurs numériques d'un JSON (nombres, et nombres écrits dans les chaînes)."""
    values = into if into is not None else set()
    if isinstance(obj, bool) or obj is None:
        return values
    if isinstance(obj, (int, float)):
        values.add(Decimal(str(obj)))
    elif isinstance(obj, str):
        for cited in extract_numbers(obj):
            values.update(v for v, _ in cited.readings)
    elif isinstance(obj, dict):
        for v in obj.values():
            known_values(v, values)
    elif isinstance(obj, (list, tuple)):
        for v in obj:
            known_values(v, values)
    return values
