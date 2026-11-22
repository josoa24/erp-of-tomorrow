from __future__ import annotations

import re
import unicodedata

_LIGATURES = str.maketrans({"œ": "oe", "æ": "ae"})
_NON_WORD = re.compile(r"[^a-z0-9]+")


def normalize(text: str) -> str:
    decomposed = unicodedata.normalize("NFKD", text.casefold().translate(_LIGATURES))
    plain = "".join(char for char in decomposed if not unicodedata.combining(char))
    return _NON_WORD.sub(" ", plain).strip()


def slugify(text: str, max_length: int = 32) -> str:
    return normalize(text).replace(" ", "-")[:max_length].strip("-")


def any_of(*fragments: str) -> re.Pattern[str]:
    return re.compile(r"\b(?:" + "|".join(fragments) + ")")
