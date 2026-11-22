"""Vérification des chiffres cités et étiquetage FACT / ESTIMATION / RECOMMENDATION / NONE."""
import json
from decimal import Decimal

from app.agent.records import ToolCallRecord
from app.trust.assessment import assess
from app.trust.numbers import extract_numbers, known_values
from tests.fake_gemini import fc_response, text_response
from tests.helpers import gateway_headers, make_app


def rec(name, result, success=True, status=None):
    return ToolCallRecord(name=name, arguments={}, success=success, duration_ms=1, turn=1, status=status,
                          result=result)


def test_number_formats():
    raws = [c.raw for c in extract_numbers("Total 1 440 000 Ar, soit 1 440 000 ; taux 12,5 % ; "
                                           "1,234.56 et 2.550.000 ; 0 ou 1 alerte")]
    assert raws == ["1 440 000", "1 440 000", "12,5", "1,234.56", "2.550.000"]
    readings = {v for c in extract_numbers("1,440") for v, _ in c.readings}
    assert readings == {Decimal(1440), Decimal("1.440")}


def test_list_markers_ignored_and_known_values():
    assert [c.raw for c in extract_numbers("1. Riz : 14 sacs\n2) Huile : 60")] == ["14", "60"]
    known = known_values({"a": 2550000, "b": [12.48, True], "c": "FA-2026-0042", "d": None})
    assert {Decimal(2550000), Decimal("12.48"), Decimal(2026), Decimal(42)} <= known
    assert Decimal(1) not in known  # True n'est pas un nombre


def test_fact_with_verified_and_unverified_numbers():
    r = assess("Valeur du stock ?", "Valeur : 2 550 000 Ar, panier moyen 103 333, marge 900 000.",
               [rec("get_stock_summary", {"totalStockValue": 2550000, "avg": 103333.33})])
    assert r == {"label": "FACT", "verifiedNumbers": ["2 550 000", "103 333"],
                 "unverifiedNumbers": ["900 000"], "sources": ["get_stock_summary"]}


def test_question_numbers_and_labels():
    pending = rec("record_stock_movement", {"status": "CONFIRMATION_REQUIRED"}, status="pending_confirmation")
    r = assess("Ajoute 50 sacs", "J'ajoute 50 sacs, confirmez-vous ?", [pending])
    assert r["label"] == "RECOMMENDATION" and r["verifiedNumbers"] == ["50"]
    assert assess("q", "2 jours", [rec("get_product_autonomy", {"daysRemaining": 2})])["label"] == "ESTIMATION"
    assert assess("Salut", "Bonjour !", [])["label"] == "NONE"
    failed = assess("q", "Erreur 42", [rec("get_stock_summary", {"success": False}, success=False)])
    assert failed["label"] == "NONE" and failed["sources"] == [] and failed["unverifiedNumbers"] == ["42"]


def test_trust_in_chat_response():
    c, _, _ = make_app([fc_response(("get_sales_summary", {"period": "this_month"})),
                        text_response("CA du mois : 310 000 Ar sur 3 ventes, objectif 500 000.")])
    with c:
        body = c.post("/agent/chat", json={"message": "CA du mois ?"}, headers=gateway_headers("VENTE")).json()
    assert body["trust"] == {"label": "FACT", "verifiedNumbers": ["310 000", "3"],
                             "unverifiedNumbers": ["500 000"], "sources": ["get_sales_summary"]}


def test_trust_in_stream_done_event():
    c, _, _ = make_app([fc_response(("get_stock_status", {})), text_response("Riz : 2 jours de stock (14 sacs).")])
    with c:
        text = c.post("/agent/chat/stream", json={"message": "Rupture ?"}, headers=gateway_headers("STOCK")).text
    done = json.loads(text.strip().split("\n\n")[-1].split("\n")[1][6:])
    assert done["trust"]["label"] == "ESTIMATION" and done["trust"]["verifiedNumbers"] == ["2", "14"]
