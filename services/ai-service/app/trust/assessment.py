"""Étiquetage de confiance d'une réponse (TRUST-07/08).

Label (premier cas qui s'applique) :
- RECOMMENDATION : une écriture a été proposée dans ce tour (en attente de confirmation) ;
- ESTIMATION     : un outil de prévision a servi (autonomie, réapprovisionnement, jours de stock restants) ;
- FACT           : au moins un outil de lecture a réussi ;
- NONE           : aucun outil (salutation, réponse générale).
Un nombre de la réponse est « vérifié » s'il apparaît dans la question ou dans un résultat d'outil
du tour (voir app/trust/numbers.py) ; sinon il est listé dans unverifiedNumbers (calcul ou invention).
"""
from __future__ import annotations

from decimal import Decimal
from typing import Any, Iterable

from app.agent.records import ToolCallRecord
from app.trust.numbers import extract_numbers, known_values

ESTIMATION_TOOLS = {"get_product_autonomy", "get_stock_status", "get_replenishment_forecast",
                    "list_replenishment_needs"}


def _label(records: list[ToolCallRecord], sources: list[str]) -> str:
    if any(r.status == "pending_confirmation" for r in records):
        return "RECOMMENDATION"
    if any(name in ESTIMATION_TOOLS for name in sources):
        return "ESTIMATION"
    return "FACT" if sources else "NONE"


def assess(question: str, answer: str, records: Iterable[ToolCallRecord]) -> dict[str, Any]:
    records = list(records)
    sources = list(dict.fromkeys(r.name for r in records if r.success))
    known: set[Decimal] = known_values(question)
    for r in records:
        known_values(r.result, known)
    verified, unverified = [], []
    for cited in extract_numbers(answer):
        (verified if cited.matches(known) else unverified).append(cited.raw)
    return {"label": _label(records, sources), "verifiedNumbers": verified,
            "unverifiedNumbers": unverified, "sources": sources}
