import pytest

from app.eclipse.catalog import adaptation_scope
from app.eclipse.matcher import match
from app.eclipse.text import normalize
from tests.eclipse_sentences import FIGMA_SENTENCES


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), FIGMA_SENTENCES)
def test_should_recognize_figma_sentence_when_typed_on_its_screen(module_key, sentence, expected_key):
    result = match(sentence, adaptation_scope(module_key, "ADMIN"))

    assert (result.intent, result.adaptation_key, result.keyword_hit) == ("ADAPTATION", expected_key, True)


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), FIGMA_SENTENCES)
def test_should_infer_target_adaptation_when_figma_sentence_typed_on_home(module_key, sentence, expected_key):
    result = match(sentence, adaptation_scope("accueil", "ADMIN"))

    assert (result.intent, result.adaptation_key) == ("ADAPTATION", expected_key)


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), [
    ("stock", "Ajouter la date d'expiration", "stock.lots-peremption"),
    ("stock", "suivre la DLC des yaourts", "stock.lots-peremption"),
    ("stock", "PÉREMPTION", "stock.lots-peremption"),
    ("ventes", "Paiement par MVola", "ventes.mobile-money-credit"),
    ("ventes", "ajoute orange money et airtel money", "ventes.mobile-money-credit"),
    ("relations", "Ajoute l'encours des clients", "relations.nif-plafond-credit"),
    ("relations", "nif et stat", "relations.nif-plafond-credit"),
    ("equipe", "Ajoute les acomptes", "equipe.avances-salaire"),
    ("equipe", "AVANCE SUR SALAIRE", "equipe.avances-salaire"),
])
def test_should_recognize_synonyms_when_accents_and_case_differ(module_key, sentence, expected_key):
    result = match(sentence, adaptation_scope(module_key, "ADMIN"))

    assert (result.intent, result.adaptation_key) == ("ADAPTATION", expected_key)


@pytest.mark.parametrize(("module_key", "sentence"), [
    ("stock", "Combien de produits expirent bientôt ?"),
    ("relations", "Quels clients dépassent leur plafond"),
    ("ventes", "Liste les ventes à crédit"),
    ("stock", "Quel est le stock de riz ?"),
    ("accueil", "Combien ai-je vendu aujourd'hui"),
])
def test_should_detect_question_when_request_is_interrogative(module_key, sentence):
    result = match(sentence, adaptation_scope(module_key, "ADMIN"))

    assert result.intent == "QUESTION"


@pytest.mark.parametrize(("module_key", "sentence"), [
    ("stock", "Ajoute une colonne couleur"),
    ("stock", "Ajoute le paiement Mobile Money"),
    ("equipe", "Mets le fond en rouge"),
    ("livraisons", "Ajoute le n° de lot"),
    ("accueil", "Ajoute le lot et l'avance"),
])
def test_should_return_unsupported_when_request_is_outside_module_catalog(module_key, sentence):
    result = match(sentence, adaptation_scope(module_key, "ADMIN"))

    assert (result.intent, result.adaptation_key) == ("UNSUPPORTED", None)


@pytest.mark.parametrize(("sentence", "keyword_hit"), [
    ("Crée un module pour suivre mes livraisons", True),
    ("Nouveau module réparations", True),
    ("Je veux suivre mes rendez-vous", False),
])
def test_should_detect_module_creation_when_user_asks_for_a_new_module(sentence, keyword_hit):
    result = match(sentence, adaptation_scope("accueil", "ADMIN"))

    assert (result.intent, result.keyword_hit) == ("CREATE_MODULE", keyword_hit)


def test_should_prefer_adaptation_when_soft_creation_names_a_catalog_field():
    result = match("Je veux suivre les lots", adaptation_scope("stock", "ADMIN"))

    assert (result.intent, result.adaptation_key) == ("ADAPTATION", "stock.lots-peremption")


def test_should_only_consider_accessible_modules_when_inferring_from_home():
    result = match("Ajoute le n° de lot et la péremption", adaptation_scope("accueil", "VENTE"))

    assert result.intent == "UNSUPPORTED"


def test_should_ignore_accents_case_and_punctuation_when_normalizing():
    assert normalize("  PÉREMPTION, n° de LOT — Œuvre !") == "peremption n de lot oeuvre"
