from __future__ import annotations

from typing import Any

RISK_TONES = {"CRITICAL": "critical", "WARNING": "warning", "OK": "success"}
RISK_LABELS = {"CRITICAL": "Rupture proche", "WARNING": "À surveiller", "OK": "Correct"}
ORDER_LABELS = {"DRAFT": "Brouillon", "VALIDATED": "Validé", "RECEIVED": "Reçu"}
ORDER_TONES = {"DRAFT": "neutral", "VALIDATED": "info", "RECEIVED": "success"}
PAYMENT_LABELS = {"DRAFT": "Brouillon", "TO_COLLECT": "À encaisser", "OVERDUE": "En retard",
                  "PAID": "Payée", "CANCELLED": "Annulée"}
PAYMENT_TONES = {"DRAFT": "neutral", "TO_COLLECT": "warning", "OVERDUE": "critical",
                 "PAID": "success", "CANCELLED": "neutral"}
EMPLOYEE_LABELS = {"PRESENT": "Présent", "ON_LEAVE": "En congé"}
EMPLOYEE_TONES = {"PRESENT": "success", "ON_LEAVE": "info"}


def column(key: str, label: str, kind: str = "text") -> dict[str, str]:
    return {"key": key, "label": label, "kind": kind}


def status(value: Any, labels: dict[str, str], tones: dict[str, str]) -> dict[str, str]:
    return {"label": labels.get(str(value), str(value)), "tone": tones.get(str(value), "neutral")}


def pick(row: dict[str, Any], *keys: str) -> dict[str, Any]:
    return {key: row.get(key) for key in keys}


def rows_of(result: dict[str, Any]) -> list[dict[str, Any]]:
    data = result.get("data")
    return [row for row in data if isinstance(row, dict)] if isinstance(data, list) else []


def table(title: str, columns: list[dict[str, str]], rows: list[dict[str, Any]], result: dict[str, Any],
          limit: int = 8) -> list[dict[str, Any]]:
    if not rows:
        return []
    total = result.get("count", len(rows))
    block: dict[str, Any] = {"type": "table", "title": title, "columns": columns, "rows": rows[:limit]}
    if isinstance(total, int) and total > limit:
        block["total"] = total
    return [block]


def kpi(title: str, items: list[tuple[str, Any, str | None, str]]) -> list[dict[str, Any]]:
    return [{"type": "kpi", "title": title, "items": [
        {"label": label, "value": value, "kind": kind, **({"hint": hint} if hint else {})}
        for label, value, hint, kind in items if value is not None]}]
