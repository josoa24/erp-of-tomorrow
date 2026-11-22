from __future__ import annotations

from typing import Any

CONSTRAINTS = frozenset(("pattern", "minLength", "maxLength", "minItems", "maxItems", "minimum", "maximum", "title"))


def simplify(node: Any) -> Any:
    if isinstance(node, dict):
        return {key: simplify(value) for key, value in node.items() if key not in CONSTRAINTS}
    if isinstance(node, list):
        return [simplify(item) for item in node]
    return node
