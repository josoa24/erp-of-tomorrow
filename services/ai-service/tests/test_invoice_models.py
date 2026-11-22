"""Schéma de sortie envoyé à Gemini (sans contrainte) et remise dans les bornes côté service."""
import json

from app.invoices.models import MAX_LINES, ExtractedInvoice, GeminiInvoice, GeminiLine

CONSTRAINTS = ("maxLength", "minLength", "maximum", "minimum", "maxItems", "minItems", "pattern")


def test_schema_sent_to_gemini_has_no_constraint():
    schema = json.dumps(GeminiInvoice.model_json_schema())
    assert not [word for word in CONSTRAINTS if f'"{word}"' in schema]


def test_valid_output_is_kept_as_is():
    raw = GeminiInvoice(supplierName="Grossiste Analamanga", invoiceNumber="FA-1", totalAmount=1508000,
                        lines=[GeminiLine(description="Riz", quantity=20, unitPrice=42000)])
    out = ExtractedInvoice.from_gemini(raw)
    assert out.supplierName == "Grossiste Analamanga" and out.totalAmount == 1508000
    assert out.lines[0].unitPrice == 42000


def test_long_texts_are_truncated_instead_of_failing():
    raw = GeminiInvoice(invoiceNumber="N" * 500, notes="x" * 9000,
                        lines=[GeminiLine(description="d" * 1000, quantity=1)])
    out = ExtractedInvoice.from_gemini(raw)
    assert len(out.invoiceNumber) == 64 and len(out.notes) == 4000 and len(out.lines[0].description) == 300


def test_negative_amounts_are_dropped_and_lines_limited():
    raw = GeminiInvoice(totalAmount=-5, lines=[GeminiLine(quantity=-1, unitPrice=10)] * (MAX_LINES + 20))
    out = ExtractedInvoice.from_gemini(raw)
    assert out.totalAmount is None
    assert len(out.lines) == MAX_LINES and out.lines[0].quantity is None and out.lines[0].unitPrice == 10
