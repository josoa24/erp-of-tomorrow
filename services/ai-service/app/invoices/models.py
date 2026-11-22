"""Facture extraite d'un document par Gemini (sortie structurée JSON, puis validée).

Deux niveaux :
- GeminiInvoice : schéma de sortie envoyé à Gemini, volontairement SANS contrainte (longueurs, bornes,
  taille de liste). Gemini refuse les schémas trop contraints (« too many states for serving »).
- ExtractedInvoice : modèle validé côté service. `from_gemini` tronque les textes, écarte les montants
  négatifs et limite le nombre de lignes avant validation : les garanties restent dans le service.
"""
from __future__ import annotations

from pydantic import BaseModel, ConfigDict, Field

MAX_LINES = 100


class GeminiLine(BaseModel):
    description: str | None = None
    quantity: float | None = None
    unitPrice: float | None = None  # noqa: N815 (nom du contrat ERP)
    amount: float | None = None


class GeminiInvoice(BaseModel):
    supplierName: str | None = None  # noqa: N815
    invoiceNumber: str | None = None  # noqa: N815
    invoiceDate: str | None = None  # noqa: N815
    bankAccount: str | None = None  # noqa: N815
    lines: list[GeminiLine] = []
    subtotal: float | None = None
    taxAmount: float | None = None  # noqa: N815
    totalAmount: float | None = None  # noqa: N815
    notes: str | None = None


class ExtractedLine(BaseModel):
    model_config = ConfigDict(extra="ignore")
    description: str | None = Field(default=None, max_length=300)
    quantity: float | None = Field(default=None, ge=0)
    unitPrice: float | None = Field(default=None, ge=0)  # noqa: N815
    amount: float | None = Field(default=None, ge=0)


class ExtractedInvoice(BaseModel):
    """Champs lus sur le document. Absent = null. Les montants sont en Ariary."""
    model_config = ConfigDict(extra="ignore")
    supplierName: str | None = Field(default=None, max_length=200)  # noqa: N815
    invoiceNumber: str | None = Field(default=None, max_length=64)  # noqa: N815
    invoiceDate: str | None = Field(default=None, max_length=32)  # noqa: N815
    bankAccount: str | None = Field(default=None, max_length=64)  # noqa: N815
    lines: list[ExtractedLine] = Field(default_factory=list, max_length=MAX_LINES)
    subtotal: float | None = Field(default=None, ge=0)
    taxAmount: float | None = Field(default=None, ge=0)  # noqa: N815
    totalAmount: float | None = Field(default=None, ge=0)  # noqa: N815
    notes: str | None = Field(default=None, max_length=4000)

    @classmethod
    def from_gemini(cls, raw: GeminiInvoice) -> "ExtractedInvoice":
        """Ramène la sortie libre de Gemini dans les bornes du modèle, puis la valide."""
        data = raw.model_dump()
        data["lines"] = [_bounded(line, ExtractedLine) for line in data["lines"][:MAX_LINES]]
        return cls.model_validate(_bounded(data, cls))


def _bounded(values: dict, model: type[BaseModel]) -> dict:
    out = dict(values)
    for name, field in model.model_fields.items():
        value = out.get(name)
        limits = {type(m).__name__: m for m in field.metadata}
        if isinstance(value, str) and "MaxLen" in limits:
            out[name] = value[:limits["MaxLen"].max_length]
        elif isinstance(value, (int, float)) and "Ge" in limits and value < limits["Ge"].ge:
            out[name] = None
    return out
