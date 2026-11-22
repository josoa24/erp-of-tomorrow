import json

from tests.eclipse_definitions import LOCATIONS
from tests.eclipse_sentences import STOCK_SENTENCE
from tests.eclipse_support import eclipse_client, propose, propose_module
from tests.fake_gemini import text_response

PARAPHRASE = "Prévenir l’équipe avant que les yaourts ne soient plus bons"


def test_should_use_gemini_classification_when_matcher_has_no_keyword_hit():
    verdict = json.dumps({"intent": "ADAPTATION", "adaptationKey": "stock.lots-peremption"})
    client, fake, _ = eclipse_client(script=[text_response(verdict)])

    with client:
        body = propose(client, "stock", PARAPHRASE).json()

    assert (body["outcome"], body["adaptationKey"]) == ("ADAPTATION", "stock.lots-peremption")
    assert "Écran : Stock" in fake.requests[0]["contents"][0].parts[0].text


def test_should_not_call_gemini_when_matcher_has_a_keyword_hit():
    client, fake, _ = eclipse_client(script=[])

    with client:
        body = propose(client, "stock", STOCK_SENTENCE).json()

    assert body["adaptationKey"] == "stock.lots-peremption" and fake.requests == []


def test_should_keep_matcher_answer_when_gemini_proposes_a_key_of_another_module():
    verdict = json.dumps({"intent": "ADAPTATION", "adaptationKey": "equipe.avances-salaire"})
    client, _, _ = eclipse_client(script=[text_response(verdict)])

    with client:
        body = propose(client, "stock", PARAPHRASE).json()

    assert body["outcome"] == "UNSUPPORTED" and "adaptationKey" not in body


def test_should_keep_matcher_answer_when_gemini_is_unavailable():
    client, _, _ = eclipse_client(script=[TimeoutError()])

    with client:
        response = propose(client, "stock", PARAPHRASE)

    assert response.status_code == 200 and response.json()["outcome"] == "UNSUPPORTED"


def test_should_use_gemini_design_when_no_template_matches():
    client, fake, _ = eclipse_client(script=[text_response(json.dumps(LOCATIONS))])

    with client:
        body = propose_module(client, "Suivre la location de matériel").json()

    assert body["source"] == "GEMINI" and body["proposal"]["name"] == "Locations"
    assert body["proposal"]["description"] == "Suivre la location de matériel" and "note" not in body
    assert len(fake.requests) == 1


def test_should_fall_back_to_generic_proposal_when_gemini_design_is_invalid():
    client, _, _ = eclipse_client(script=[text_response(json.dumps({**LOCATIONS, "icon": "rocket"}))])

    with client:
        body = propose_module(client, "Suivre la location de matériel").json()

    assert body["source"] == "TEMPLATE" and body["note"].startswith("Eclipse n’a pas de modèle")


def test_should_not_call_gemini_when_a_template_matches():
    client, fake, _ = eclipse_client(script=[])

    with client:
        body = propose_module(client, "Prendre les rendez-vous des clients").json()

    assert body["proposal"]["name"] == "Rendez-vous" and fake.requests == []
