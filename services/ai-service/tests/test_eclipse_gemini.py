import asyncio
import json

from app.config import Settings
from app.eclipse.catalog import adaptation_scope
from app.eclipse.gemini_designer import ModuleDesigner
from app.eclipse.gemini_intent import IntentClassifier
from app.eclipse.matcher import Match
from tests.eclipse_definitions import LOCATIONS
from tests.fake_gemini import FakeClient, text_response

SETTINGS = Settings(gemini_api_key="", gemini_model="t", ai_audit_db=":memory:")
STOCK_SCOPE = adaptation_scope("stock", "ADMIN")


def classify(script, scope=STOCK_SCOPE):
    fake = FakeClient(script)
    result = asyncio.run(IntentClassifier(fake, SETTINGS).classify("Prévenir avant la fin des yaourts", "Stock",
                                                                   scope))
    return result, fake


def design(script):
    fake = FakeClient(script)
    return asyncio.run(ModuleDesigner(fake, SETTINGS).design("Suivre la location de matériel")), fake


def test_should_return_adaptation_when_gemini_picks_a_key_in_scope():
    verdict = json.dumps({"intent": "ADAPTATION", "adaptationKey": "stock.lots-peremption"})

    result, _ = classify([text_response(verdict)])

    assert result == Match("ADAPTATION", "stock.lots-peremption")


def test_should_constrain_gemini_to_the_closed_catalog_when_classifying():
    _, fake = classify([text_response(json.dumps({"intent": "QUESTION"}))])

    config = fake.requests[0]["config"]
    assert config.temperature == 0 and config.response_mime_type == "application/json"
    assert config.response_json_schema["properties"]["adaptationKey"]["anyOf"][0]["enum"] == ["stock.lots-peremption"]
    assert "stock.lots-peremption" in config.system_instruction and "DONNÉE" in config.system_instruction


def test_should_return_question_when_gemini_classifies_a_question():
    result, _ = classify([text_response(json.dumps({"intent": "QUESTION", "adaptationKey": None}))])

    assert result == Match("QUESTION")


def test_should_reject_gemini_answer_when_key_is_outside_module_scope():
    verdict = json.dumps({"intent": "ADAPTATION", "adaptationKey": "ventes.mobile-money-credit"})

    result, _ = classify([text_response(verdict)])

    assert result is None


def test_should_return_none_when_gemini_answer_is_not_valid_json():
    result, _ = classify([text_response("ADAPTATION")])

    assert result is None


def test_should_return_none_when_gemini_call_fails():
    result, _ = classify([TimeoutError()])

    assert result is None


def test_should_return_validated_definition_when_gemini_designs_a_module():
    definition, fake = design([text_response(json.dumps(LOCATIONS))])

    assert definition.name == "Locations" and [c.key for c in definition.columns] == ["materiel", "caution"]
    assert fake.requests[0]["config"].response_json_schema["properties"]["icon"]["enum"][0] == "truck"


def test_should_return_none_when_gemini_icon_is_outside_allow_list():
    definition, _ = design([text_response(json.dumps({**LOCATIONS, "icon": "rocket"}))])

    assert definition is None


def test_should_return_none_when_gemini_design_call_fails():
    definition, _ = design([RuntimeError("réseau")])

    assert definition is None
