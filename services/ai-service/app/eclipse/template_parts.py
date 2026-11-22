from __future__ import annotations

import re
from dataclasses import dataclass
from typing import Any

from app.eclipse.definition import ModuleDefinitionRequest


@dataclass(frozen=True)
class ModuleTemplate:
    pattern: re.Pattern[str]
    definition: ModuleDefinitionRequest
    unchecked_columns: tuple[str, ...] = ()
    hidden_columns: tuple[str, ...] = ()


def column(key: str, label: str, type_: str, reference: str | None = None,
           required: bool = False) -> dict[str, Any]:
    spec: dict[str, Any] = {"key": key, "label": label, "type": type_, "required": required}
    return {**spec, "reference": reference} if reference else spec


def status(key: str, label: str, tone: str, final: bool = False) -> dict[str, Any]:
    return {"key": key, "label": label, "tone": tone, "final": final}


def indicator(key: str, label: str, icon: str, kind: str, **scope: Any) -> dict[str, Any]:
    return {"key": key, "label": label, "icon": icon, "kind": kind, **scope}


def definition(name: str, icon: str, columns: list[dict[str, Any]], statuses: list[dict[str, Any]],
               indicators: list[dict[str, Any]]) -> ModuleDefinitionRequest:
    return ModuleDefinitionRequest.model_validate(
        {"name": name, "icon": icon, "columns": columns, "statuses": statuses, "indicators": indicators})
