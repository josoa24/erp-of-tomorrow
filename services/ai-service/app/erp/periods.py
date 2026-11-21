"""Conversion d'une période en dates `from` / `to` (format AAAA-MM-JJ attendu par l'ERP)."""
from __future__ import annotations

from datetime import date, timedelta
from typing import Literal

Period = Literal["today", "yesterday", "this_week", "last_week", "this_month",
                 "last_month", "last_7_days", "last_30_days", "all", "custom"]


class PeriodError(ValueError):
    pass


def resolve(period: str, start: str | None = None, end: str | None = None,
            today: date | None = None) -> tuple[str | None, str | None]:
    t = today or date.today()
    if period == "all":
        return None, None
    if period == "custom":
        if not start or not end:
            raise PeriodError("La période 'custom' exige start_date et end_date (AAAA-MM-JJ).")
        s, e = date.fromisoformat(start), date.fromisoformat(end)
        if s > e:
            raise PeriodError("start_date doit précéder end_date.")
        return s.isoformat(), e.isoformat()
    ranges = {
        "today": (t, t),
        "yesterday": (t - timedelta(days=1), t - timedelta(days=1)),
        "this_week": (t - timedelta(days=t.weekday()), t),
        "last_week": (t - timedelta(days=t.weekday() + 7), t - timedelta(days=t.weekday() + 1)),
        "this_month": (t.replace(day=1), t),
        "last_month": ((t.replace(day=1) - timedelta(days=1)).replace(day=1), t.replace(day=1) - timedelta(days=1)),
        "last_7_days": (t - timedelta(days=6), t),
        "last_30_days": (t - timedelta(days=29), t),
    }
    if period not in ranges:
        raise PeriodError(f"Période inconnue : {period}")
    s, e = ranges[period]
    return s.isoformat(), e.isoformat()
