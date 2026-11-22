from __future__ import annotations

from dataclasses import dataclass
from typing import Literal, Sequence

from app.eclipse.catalog import Adaptation
from app.eclipse.intent_patterns import (ADD_VERBS, BUSINESS_ACTION, EXPLICIT_CREATE, PERIOD_WORDS, QUESTION_START,
                                         QUESTION_WORDS, REMOVAL_VERBS, SOFT_CREATE, WISH)
from app.eclipse.keywords import ADAPTATION_KEYWORDS, MIN_SCORE
from app.eclipse.text import normalize

Intent = Literal["ADAPTATION", "QUESTION", "CREATE_MODULE", "UNSUPPORTED"]


@dataclass(frozen=True)
class Match:
    intent: Intent
    adaptation_key: str | None = None
    keyword_hit: bool = False
    removal: bool = False


def score(normalized: str, adaptation_key: str) -> int:
    keywords = ADAPTATION_KEYWORDS.get(adaptation_key, ())
    return sum(weight for pattern, weight in keywords if pattern.search(normalized))


def best_adaptation(normalized: str, candidates: Sequence[Adaptation]) -> str | None:
    ranked = sorted(((score(normalized, a.key), a.key) for a in candidates), reverse=True)
    if not ranked or ranked[0][0] < MIN_SCORE:
        return None
    if len(ranked) > 1 and ranked[1][0] == ranked[0][0]:
        return None
    return ranked[0][1]


def is_question(request: str, normalized: str) -> bool:
    if "?" in request or QUESTION_START.search(normalized):
        return True
    return QUESTION_WORDS.search(normalized) is not None or PERIOD_WORDS.search(normalized) is not None


def match(request: str, candidates: Sequence[Adaptation]) -> Match:
    normalized = normalize(request)
    if EXPLICIT_CREATE.search(normalized):
        return Match("CREATE_MODULE", keyword_hit=True)
    if BUSINESS_ACTION.search(normalized):
        return Match("QUESTION", keyword_hit=True)
    adaptation_key = best_adaptation(normalized, candidates)
    adding = ADD_VERBS.search(normalized) is not None
    if adaptation_key and adding:
        return Match("ADAPTATION", adaptation_key, keyword_hit=True)
    if is_question(request, normalized):
        return Match("QUESTION", keyword_hit=adaptation_key is not None)
    if not adding and REMOVAL_VERBS.search(normalized):
        return Match("UNSUPPORTED", keyword_hit=True, removal=True)
    if adaptation_key:
        return Match("ADAPTATION", adaptation_key, keyword_hit=WISH.search(normalized) is not None)
    if SOFT_CREATE.search(normalized):
        return Match("CREATE_MODULE")
    return Match("UNSUPPORTED")
