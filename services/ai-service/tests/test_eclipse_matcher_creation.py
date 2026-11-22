import pytest

from app.eclipse.catalog import adaptation_scope
from app.eclipse.matcher import match

STOCK = "stock.lots-peremption"
VENTES = "ventes.mobile-money-credit"
RELATIONS = "relations.nif-plafond-credit"
EQUIPE = "equipe.avances-salaire"


@pytest.mark.parametrize(("module_key", "sentence", "expected_key"), [
    ("stock", "Ajoute dans ce module le numéro de lot", STOCK),
    ("stock", "Ajoute au module la date de péremption", STOCK),
    ("stock", "Ajoute au module stock le n° de lot et la péremption", STOCK),
    ("stock", "Ajoute un nouveau champ lot dans le module", STOCK),
    ("ventes", "Ajoute dans ce module le paiement MVola", VENTES),
    ("ventes", "Ajoute au module le paiement Orange Money", VENTES),
    ("ventes", "Ajoute à ce module le paiement mobile money", VENTES),
    ("relations", "Ajoute dans ce module le NIF et le STAT", RELATIONS),
    ("relations", "Ajoute au module un plafond de crédit", RELATIONS),
    ("equipe", "Ajoute dans ce module les avances sur salaire", EQUIPE),
    ("equipe", "Ajoute au module les acomptes", EQUIPE),
    ("accueil", "Ajoute dans le module Ventes le paiement MVola", VENTES),
    ("accueil", "Ajoute au nouveau module Stock la péremption", STOCK),
])
def test_should_propose_adaptation_when_request_only_mentions_a_module(module_key, sentence, expected_key):
    result = match(sentence, adaptation_scope(module_key, "ADMIN"))

    assert (result.intent, result.adaptation_key, result.keyword_hit) == ("ADAPTATION", expected_key, True)


@pytest.mark.parametrize(("module_key", "sentence"), [
    ("stock", "Crée-moi un module pour les réparations"),
    ("ventes", "Créer un nouveau module"),
    ("relations", "Ajoute un module de livraison"),
    ("equipe", "Fais un module pour les rendez-vous"),
    ("accueil", "Création d'un module de suivi des colis"),
    ("accueil", "Prépare un autre module"),
])
def test_should_detect_module_creation_when_module_follows_a_creation_marker(module_key, sentence):
    result = match(sentence, adaptation_scope(module_key, "ADMIN"))

    assert (result.intent, result.keyword_hit) == ("CREATE_MODULE", True)
