"""Définition commune d'un outil : déclaration Gemini + validation + exécution."""
from __future__ import annotations

from dataclasses import dataclass
from typing import Any, Awaitable, Callable

from google.genai import types
from pydantic import BaseModel, ConfigDict

S, T = types.Schema, types.Type


class Args(BaseModel):
    """Base des arguments : champs inconnus ignorés (jamais exécutés), espaces retirés."""
    model_config = ConfigDict(extra="ignore", str_strip_whitespace=True)


class NoArgs(Args):
    pass


@dataclass(frozen=True)
class ToolSpec:
    declaration: types.FunctionDeclaration
    args_model: type[BaseModel]
    handler: Callable[[Any, Any], Awaitable[dict[str, Any]]] | None
    module: str  # stock | vente | rh | crm | system  -> détermine les rôles autorisés
    # Outil d'écriture : fonction qui produit le récapitulatif montré avant confirmation.
    summarize: Callable[[Any], str] | None = None

    @property
    def name(self) -> str:
        return self.declaration.name  # type: ignore[return-value]

    @property
    def requires_confirmation(self) -> bool:
        return self.summarize is not None


def decl(name: str, description: str, properties: dict[str, types.Schema] | None = None,
         required: list[str] | None = None) -> types.FunctionDeclaration:
    params = S(type=T.OBJECT, properties=properties, required=required or []) if properties else None
    return types.FunctionDeclaration(name=name, description=description, parameters=params)


def string(description: str, enum: list[str] | None = None) -> types.Schema:
    return S(type=T.STRING, description=description, enum=enum)


def integer(description: str) -> types.Schema:
    return S(type=T.INTEGER, description=description)


PERIOD = string("Période. all = depuis toujours ; custom exige start_date et end_date.",
                ["today", "yesterday", "this_week", "last_week", "this_month", "last_month",
                 "last_7_days", "last_30_days", "all", "custom"])
DATE_START = string("AAAA-MM-JJ, uniquement si period=custom.")
DATE_END = string("AAAA-MM-JJ, uniquement si period=custom.")
WRITE = " Écriture : soumise à la confirmation de l'utilisateur."
