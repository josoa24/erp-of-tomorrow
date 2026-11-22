import pytest

from app.eclipse.generic_module import DEFAULT_NAME, generic_definition, module_name
from app.eclipse.templates import LIVRAISONS, choose_template


def test_should_match_figma_04b_columns_when_livraisons_template_is_used():
    labels = [(c.label, c.type, c.reference) for c in LIVRAISONS.definition.columns]

    assert labels == [("Client", "REFERENCE", "CUSTOMER"), ("Vente d’origine", "REFERENCE", "SALE"),
                      ("Quartier", "TEXT", None), ("Livreur", "REFERENCE", "EMPLOYEE"),
                      ("Heure prévue", "TIME", None), ("Montant à encaisser", "MONEY", None),
                      ("Signature du client", "TEXT", None)]
    assert LIVRAISONS.unchecked_columns == ("signature_client",)
    assert [c.key for c in LIVRAISONS.definition.columns if not c.listed] == [
        "vente_origine", "montant_a_encaisser", "signature_client"]


def test_should_order_livraisons_statuses_as_a_workflow_ending_on_delivered():
    statuses = [(s.label, s.final) for s in LIVRAISONS.definition.statuses]

    assert statuses == [("À préparer", False), ("En route", False), ("Livrée", True)]


def test_should_compute_livraisons_indicators_from_figma_04d():
    indicators = {i.label: (i.kind, i.statuses, i.column, i.today_column, i.detail_column)
                  for i in LIVRAISONS.definition.indicators}

    assert indicators == {
        "Livrées aujourd’hui": ("RATIO_TODAY", ["livree"], None, "heure_prevue", None),
        "En route": ("COUNT", ["en_route"], None, None, "livreur"),
        "À encaisser": ("SUM", ["a_preparer", "en_route"], "montant_a_encaisser", None, None),
    }


@pytest.mark.parametrize(("description", "expected"), [
    ("Suivre les livraisons de mes clients : quartier, livreur, heure prévue", "Livraisons"),
    ("Les colis à livrer en ville", "Livraisons"),
    ("Suivre les réparations de téléphones de l'atelier", "Réparations"),
    ("Prendre les rendez-vous des clients au salon", "Rendez-vous"),
    ("Gérer les RDV du cabinet", "Rendez-vous"),
])
def test_should_choose_template_when_description_has_its_keywords(description, expected):
    assert choose_template(description).definition.name == expected


def test_should_not_choose_template_when_description_matches_none():
    assert choose_template("Suivre les commandes spéciales de mes clients") is None


@pytest.mark.parametrize(("description", "expected"), [
    ("Je veux suivre les commandes spéciales de mes clients", "Commandes spéciales"),
    ("Crée un module pour les locations de matériel", "Locations"),
    ("Créer un module", DEFAULT_NAME),
    ("Je veux suivre mon stock", DEFAULT_NAME),
    ("Gérer l'équipe", DEFAULT_NAME),
])
def test_should_derive_module_name_when_no_template_matches(description, expected):
    assert module_name(description) == expected


def test_should_build_minimal_generic_definition_when_no_template_matches():
    definition = generic_definition("Suivre les commandes spéciales")

    assert [c.label for c in definition.columns] == ["Nom", "Date", "Montant"]
    assert [s.label for s in definition.statuses] == ["À faire", "En cours", "Terminé"]
    assert [(i.kind, i.statuses) for i in definition.indicators] == [("COUNT", ["a_faire", "en_cours"])]
