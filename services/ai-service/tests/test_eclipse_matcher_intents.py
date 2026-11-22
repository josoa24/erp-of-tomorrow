import pytest

from app.eclipse.catalog import adaptation_scope
from app.eclipse.matcher import match


def recognize(module_key, sentence):
    return match(sentence, adaptation_scope(module_key, "ADMIN"))


@pytest.mark.parametrize(("module_key", "sentence"), [
    ("stock", "Supprime la colonne N° de lot"),
    ("stock", "Enlève la péremption"),
    ("equipe", "Retire les avances sur salaire"),
    ("ventes", "Cache la colonne Paiement"),
    ("relations", "Désactive le plafond de crédit"),
    ("accueil", "Masque le NIF des clients"),
])
def test_should_refuse_honestly_when_request_asks_to_remove_an_element(module_key, sentence):
    result = recognize(module_key, sentence)

    assert (result.intent, result.adaptation_key, result.removal, result.keyword_hit) == (
        "UNSUPPORTED", None, True, True)


@pytest.mark.parametrize(("module_key", "sentence"), [
    ("ventes", "Enregistre une vente de 3 sacs de riz à crédit pour Rasoa Andria"),
    ("ventes", "Fais une vente à crédit à Hery Rabe"),
    ("ventes", "Crée une vente à crédit"),
    ("ventes", "Vends 2 bidons d'huile à Toky Rakoto en MVola"),
    ("ventes", "Nouvelle vente à crédit pour Rasoa Andria"),
    ("relations", "Ajoute un client professionnel : Rasoa Andria à Antsirabe"),
    ("stock", "Peux-tu enregistrer l'arrivée du lot 24-117 ?"),
])
def test_should_send_business_action_to_the_chat_when_request_records_an_operation(module_key, sentence):
    result = recognize(module_key, sentence)

    assert (result.intent, result.adaptation_key, result.keyword_hit) == ("QUESTION", None, True)


@pytest.mark.parametrize(("module_key", "sentence"), [
    ("ventes", "ventes à crédit de cette semaine"),
    ("stock", "produits à commander cette semaine"),
    ("livraisons", "tournées de demain par quartier"),
    ("relations", "Qui me doit de l'argent"),
    ("equipe", "Quand Toky Rakoto a-t-il demandé son avance"),
    ("equipe", "Ai-je des avances à valider"),
    ("stock", "Je veux voir les lots qui périment"),
])
def test_should_detect_question_when_request_is_a_data_query(module_key, sentence):
    assert recognize(module_key, sentence).intent == "QUESTION"


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), [
    ("stock", "Il me faut la date de péremption des produits qui arrivent", "stock.lots-peremption"),
    ("stock", "Je veux suivre les produits qui périment", "stock.lots-peremption"),
    ("stock", "Préviens-moi quand un lot arrive à péremption", "stock.lots-peremption"),
    ("stock", "Fais apparaître la péremption dans le module", "stock.lots-peremption"),
    ("ventes", "Les clients paient en MVola, il faut le noter", "ventes.mobile-money-credit"),
])
def test_should_propose_adaptation_when_wish_uses_a_relative_clause(module_key, sentence, expected_key):
    result = recognize(module_key, sentence)

    assert (result.intent, result.adaptation_key, result.keyword_hit) == ("ADAPTATION", expected_key, True)


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), [
    ("stock", "PÉREMPTION", "stock.lots-peremption"),
    ("ventes", "Mes clients paient aussi en mvola et à crédit", "ventes.mobile-money-credit"),
])
def test_should_let_gemini_decide_when_request_names_a_field_without_a_verb(module_key, sentence, expected_key):
    result = recognize(module_key, sentence)

    assert (result.intent, result.adaptation_key, result.keyword_hit) == ("ADAPTATION", expected_key, False)
