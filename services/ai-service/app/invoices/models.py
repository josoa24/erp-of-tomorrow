"""Facture extraite d'un document par Gemini (sortie structurée JSON validée)."""
from __future__ import annotations

from pydantic import BaseModel, ConfigDict, Field


class ExtractedLine(BaseModel):
    model_config = ConfigDict(extra="ignore")
    description: str | None = Field(default=None, max_length=300)
    quantity: float | None = Field(default=None, ge=0)
    unitPrice: float | None = Field(default=None, ge=0)  # noqa: N815 (nom du contrat ERP)
    amount: float | None = Field(default=None, ge=0)


class ExtractedInvoice(BaseModel):
    """Champs lus sur le document. Absent = null. Les montants sont en Ariary."""
    model_config = ConfigDict(extra="ignore")
    supplierName: str | None = Field(default=None, max_length=200)  # noqa: N815
    invoiceNumber: str | None = Field(default=None, max_length=64)  # noqa: N815
    invoiceDate: str | None = Field(default=None, max_length=32)  # noqa: N815
    bankAccount: str | None = Field(default=None, max_length=64)  # noqa: N815
    lines: list[ExtractedLine] = Field(default_factory=list, max_length=100)
    subtotal: float | None = Field(default=None, ge=0)
    taxAmount: float | None = Field(default=None, ge=0)  # noqa: N815
    totalAmount: float | None = Field(default=None, ge=0)  # noqa: N815
    notes: str | None = Field(default=None, max_length=4000)
