import pytest

from app.eclipse.catalog import adaptation_scope
from app.eclipse.matcher import match


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), [
    ("stock", "Ajoute l'alerte J-30", "stock.lots-peremption"),
    ("ventes", "Ajoute la colonne Paiement", "ventes.mobile-money-credit"),
    ("ventes", "Ajoute le paiement en espèces", "ventes.mobile-money-credit"),
    ("ventes", "Ajoute le filtre À crédit", "ventes.mobile-money-credit"),
    ("relations", "Ajoute le filtre Professionnels", "relations.nif-plafond-credit"),
    ("relations", "Ajoute une alerte de dépassement", "relations.nif-plafond-credit"),
    ("relations", "Ajoute les clients pros", "relations.nif-plafond-credit"),
    ("equipe", "Ajoute la validation par le gérant", "equipe.avances-salaire"),
    ("accueil", "Ajoute la validation des avances par le gérant", "equipe.avances-salaire"),
    ("accueil", "Ajoute une alerte de dépassement", "relations.nif-plafond-credit"),
])
def test_should_recognize_the_figma_bar_vocabulary_when_typed_on_its_screen(module_key, sentence, expected_key):
    result = match(sentence, adaptation_scope(module_key, "ADMIN"))

    assert (result.intent, result.adaptation_key, result.keyword_hit) == ("ADAPTATION", expected_key, True)


@pytest.mark.parametrize(("module_key", "sentence"), [
    ("accueil", "Ajoute le paiement des salaires"),
    ("accueil", "Ajoute un filtre produits en rupture"),
    ("relations", "Ajoute le filtre À crédit"),
])
def test_should_stay_unsupported_when_vocabulary_belongs_to_no_single_adaptation(module_key, sentence):
    assert match(sentence, adaptation_scope(module_key, "ADMIN")).intent == "UNSUPPORTED"
