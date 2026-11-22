import pytest
from pydantic import ValidationError

from app.eclipse.definition import ModuleDefinitionRequest
from app.eclipse.module_slug import slug_from_name
from tests.test_eclipse_definition import valid_definition

DAY = {"key": "retour", "label": "Retour prévu", "type": "DATE"}
NOTE = {"key": "note", "label": "Note", "type": "TEXT"}


def rejected(match, **overrides):
    with pytest.raises(ValidationError, match=match):
        ModuleDefinitionRequest.model_validate(valid_definition(**overrides))


def test_should_list_column_by_default_and_keep_an_explicit_hidden_flag():
    columns = [NOTE, {**DAY, "listed": False}]

    definition = ModuleDefinitionRequest.model_validate(valid_definition(columns=columns, indicators=[]))

    assert [c.listed for c in definition.columns] == [True, False]
    assert definition.model_dump(by_alias=True)["columns"][1]["listed"] is False


def test_should_reject_definition_when_no_column_is_listed():
    rejected("affichée dans la liste", columns=[{**NOTE, "listed": False}], indicators=[])


def test_should_reject_choice_column_when_a_choice_is_repeated():
    rejected("deux fois", columns=[{"key": "mode", "label": "Mode", "type": "CHOICE", "choices": ["Moto", " Moto "]}],
             indicators=[])


@pytest.mark.parametrize("choices", [["Moto", "  "], [f"choix {n}" for n in range(21)], ["x" * 61]])
def test_should_reject_choice_column_when_choices_break_their_limits(choices):
    rejected("choices", columns=[{"key": "mode", "label": "Mode", "type": "CHOICE", "choices": choices}],
             indicators=[])


def test_should_reject_indicator_when_counted_column_is_unknown():
    indicator = {"key": "n", "label": "N", "icon": "clock", "kind": "COUNT", "column": "absente"}

    rejected("column inconnue", indicators=[indicator])


def test_should_reject_ratio_today_when_it_has_no_statuses():
    indicator = {"key": "jour", "label": "Jour", "icon": "clock", "kind": "RATIO_TODAY", "todayColumn": "retour"}

    rejected("todayColumn et des statuts", indicators=[indicator])


def test_should_reject_count_indicator_when_today_column_is_not_a_day():
    indicator = {"key": "n", "label": "N", "icon": "clock", "kind": "COUNT", "todayColumn": "caution"}

    rejected("date ou heure", indicators=[indicator])


def test_should_reject_indicator_when_it_lists_more_than_six_statuses():
    indicator = {"key": "n", "label": "N", "icon": "clock", "kind": "COUNT", "statuses": ["sortie"] * 7}

    rejected("statuses", indicators=[indicator])


@pytest.mark.parametrize("name", ["Stock", "Ventes", "Relations", "Équipe", " équipe "])
def test_should_reject_definition_when_name_is_a_built_in_module(name):
    rejected("réservé", name=name)


def test_should_reject_definition_when_name_has_no_letter_or_digit():
    rejected("au moins une lettre", name="!! --")


def test_should_slug_name_like_the_module_service():
    assert slug_from_name("  Réparations d'Été  ") == "reparations-d-ete"
