import pytest

from tests.eclipse_sentences import FIGMA_SENTENCES, STOCK_SENTENCE, VENTES_SENTENCE
from tests.eclipse_support import eclipse_client, last_audit, propose
from tests.fake_modules_erp import FakeModulesErp

SUMMARIES = {
    "stock": "Eclipse a ajouté : colonnes N° de lot et Péremption · alerte J-30 · filtre Expire bientôt",
    "ventes": "Eclipse a ajouté : colonne Paiement · filtre À crédit · indicateur Crédit en cours",
    "relations": "Eclipse a ajouté : colonnes NIF / STAT et Plafond crédit · filtre Professionnels · "
                 "alerte de dépassement",
    "equipe": "Eclipse a ajouté : colonnes Avance et Plafond · filtre Avance demandée · validation par le gérant",
}


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), FIGMA_SENTENCES)
def test_should_propose_figma_adaptation_when_admin_types_the_figma_sentence(module_key, sentence, expected_key):
    client, _, _ = eclipse_client()

    with client:
        body = propose(client, module_key, sentence).json()

    assert body == {"outcome": "ADAPTATION", "adaptationKey": expected_key, "request": sentence,
                    "title": body["title"], "summary": SUMMARIES[module_key], "canApply": True}


def test_should_not_allow_apply_when_role_is_not_admin():
    client, _, _ = eclipse_client()

    with client:
        body = propose(client, "stock", STOCK_SENTENCE, role="STOCK").json()

    assert (body["outcome"], body["canApply"]) == ("ADAPTATION", False)


def test_should_return_already_active_when_module_service_reports_the_adaptation():
    client, _, erp = eclipse_client(FakeModulesErp(active=("ventes.mobile-money-credit",)))

    with client:
        body = propose(client, "ventes", VENTES_SENTENCE, role="VENTE").json()

    assert body["outcome"] == "ALREADY_ACTIVE" and body["adaptationKey"] == "ventes.mobile-money-credit"
    assert body["message"] == ("« Paiement Mobile Money et ventes à crédit » est déjà appliqué pour l’équipe "
                               "dans « Ventes ».")
    assert erp.requests[0].headers["authorization"] == "Bearer tok-VENTE"


def test_should_assume_not_active_when_module_service_is_down():
    client, _, _ = eclipse_client(FakeModulesErp(down=True))

    with client:
        response = propose(client, "stock", STOCK_SENTENCE)

    assert response.status_code == 200 and response.json()["outcome"] == "ADAPTATION"


def test_should_return_target_module_when_request_is_typed_on_home():
    client, _, _ = eclipse_client()

    with client:
        body = propose(client, "accueil", "Ajoute les avances sur salaire, plafonnées à 30 % du salaire").json()

    assert (body["outcome"], body["targetModuleKey"]) == ("ADAPTATION", "equipe")


def test_should_return_question_without_calling_module_service_when_user_asks_a_question():
    client, _, erp = eclipse_client()

    with client:
        body = propose(client, "stock", "Combien de produits expirent bientôt ?").json()

    assert body == {"outcome": "QUESTION", "request": "Combien de produits expirent bientôt ?", "canApply": True}
    assert erp.requests == []


def test_should_record_proposed_adaptation_in_audit_when_adaptation_is_recognized():
    client, _, _ = eclipse_client()

    with client:
        propose(client, "stock", STOCK_SENTENCE)
        entry = last_audit(client)

    assert entry["tools"] == ["eclipse_propose_adaptation"] and entry["trust_label"] == "RECOMMENDATION"
    assert entry["actions"]["proposed"] == [{"tool": "eclipse_propose_adaptation",
                                             "action_id": "stock.lots-peremption"}]
