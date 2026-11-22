from __future__ import annotations

from typing import Sequence

from app.eclipse.definition_parts import ColumnSpec, IndicatorSpec, StatusSpec

DAY_TYPES = {"DATE", "DATETIME", "TIME"}


def _require_unique(kind: str, keys: list[str]) -> None:
    duplicates = sorted({key for key in keys if keys.count(key) > 1})
    if duplicates:
        raise ValueError(f"{kind} en double : {', '.join(duplicates)}")


def check_workflow(statuses: Sequence[StatusSpec]) -> None:
    if statuses[0].final:
        raise ValueError("le premier statut ne peut pas être final")
    if not any(status.final for status in statuses):
        raise ValueError("au moins un statut doit être final")


def check_indicator(indicator: IndicatorSpec, columns: dict[str, ColumnSpec], status_keys: set[str]) -> None:
    unknown = set(indicator.statuses or []) - status_keys
    if unknown:
        raise ValueError(f"indicateur {indicator.key} : statuts inconnus {', '.join(sorted(unknown))}")
    money = columns.get(indicator.column or "")
    if indicator.kind == "SUM" and (money is None or money.type != "MONEY"):
        raise ValueError(f"indicateur {indicator.key} : column doit désigner une colonne MONEY")
    day = columns.get(indicator.today_column or "")
    if indicator.kind == "RATIO_TODAY" and (day is None or day.type not in DAY_TYPES):
        raise ValueError(f"indicateur {indicator.key} : todayColumn doit désigner une colonne date ou heure")
    if indicator.detail_column and indicator.detail_column not in columns:
        raise ValueError(f"indicateur {indicator.key} : detailColumn inconnue")


def check_definition(columns: Sequence[ColumnSpec], statuses: Sequence[StatusSpec],
                     indicators: Sequence[IndicatorSpec]) -> None:
    _require_unique("colonnes", [column.key for column in columns])
    _require_unique("statuts", [status.key for status in statuses])
    _require_unique("indicateurs", [indicator.key for indicator in indicators])
    check_workflow(statuses)
    by_key = {column.key: column for column in columns}
    status_keys = {status.key for status in statuses}
    for indicator in indicators:
        check_indicator(indicator, by_key, status_keys)
