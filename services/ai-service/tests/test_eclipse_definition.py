import pytest
from pydantic import ValidationError

from app.eclipse.definition import ModuleDefinitionRequest


def valid_definition(**overrides):
    payload = {
        "name": "Locations",
        "icon": "package",
        "columns": [{"key": "client", "label": "Client", "type": "REFERENCE", "reference": "CUSTOMER"},
                    {"key": "retour", "label": "Retour prévu", "type": "DATE"},
                    {"key": "caution", "label": "Caution", "type": "MONEY"}],
        "statuses": [{"key": "sortie", "label": "Sortie", "tone": "INFO"},
                     {"key": "rendue", "label": "Rendue", "tone": "SUCCESS", "final": True}],
        "indicators": [{"key": "cautions", "label": "Cautions", "icon": "wallet", "kind": "SUM",
                        "column": "caution", "statuses": ["sortie"]}],
    }
    return {**payload, **overrides}


def test_should_accept_definition_when_camel_case_payload_is_coherent():
    definition = ModuleDefinitionRequest.model_validate(valid_definition())

    assert definition.indicators[0].column == "caution"
    assert definition.model_dump(by_alias=True, exclude_none=True)["columns"][0]["reference"] == "CUSTOMER"


def test_should_reject_definition_when_sum_targets_a_non_money_column():
    indicator = {"key": "total", "label": "Total", "icon": "receipt", "kind": "SUM", "column": "retour"}

    with pytest.raises(ValidationError, match="MONEY"):
        ModuleDefinitionRequest.model_validate(valid_definition(indicators=[indicator]))


def test_should_reject_definition_when_ratio_today_has_no_day_column():
    indicator = {"key": "jour", "label": "Jour", "icon": "clock", "kind": "RATIO_TODAY", "statuses": ["rendue"]}

    with pytest.raises(ValidationError, match="todayColumn"):
        ModuleDefinitionRequest.model_validate(valid_definition(indicators=[indicator]))


def test_should_reject_definition_when_column_keys_are_duplicated():
    columns = [{"key": "nom", "label": "Nom", "type": "TEXT"}, {"key": "nom", "label": "Nom 2", "type": "TEXT"}]

    with pytest.raises(ValidationError, match="en double"):
        ModuleDefinitionRequest.model_validate(valid_definition(columns=columns, indicators=[]))


def test_should_reject_definition_when_no_status_is_final():
    statuses = [{"key": "a", "label": "A", "tone": "INFO"}, {"key": "b", "label": "B", "tone": "SUCCESS"}]

    with pytest.raises(ValidationError, match="final"):
        ModuleDefinitionRequest.model_validate(valid_definition(statuses=statuses, indicators=[]))


def test_should_reject_definition_when_indicator_uses_unknown_status():
    indicator = {"key": "n", "label": "N", "icon": "clock", "kind": "COUNT", "statuses": ["perdue"]}

    with pytest.raises(ValidationError, match="statuts inconnus"):
        ModuleDefinitionRequest.model_validate(valid_definition(indicators=[indicator]))


def test_should_reject_column_when_choice_has_no_choices():
    columns = [{"key": "etat", "label": "État", "type": "CHOICE"}]

    with pytest.raises(ValidationError, match="choices"):
        ModuleDefinitionRequest.model_validate(valid_definition(columns=columns, indicators=[]))


def test_should_reject_column_when_reference_target_is_missing():
    columns = [{"key": "client", "label": "Client", "type": "REFERENCE"}]

    with pytest.raises(ValidationError, match="reference"):
        ModuleDefinitionRequest.model_validate(valid_definition(columns=columns, indicators=[]))


@pytest.mark.parametrize("field, value", [("icon", "rocket"), ("name", "X"), ("statuses", [])])
def test_should_reject_definition_when_field_breaks_the_contract(field, value):
    with pytest.raises(ValidationError):
        ModuleDefinitionRequest.model_validate(valid_definition(**{field: value}))
