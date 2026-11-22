from tests.eclipse_support import eclipse_client, propose
from tests.fake_modules_erp import FakeModulesErp


def test_should_return_honest_message_when_request_is_outside_the_catalog():
    client, _, _ = eclipse_client()

    with client:
        body = propose(client, "stock", "Ajoute une colonne couleur").json()

    assert body["outcome"] == "UNSUPPORTED" and "adaptationKey" not in body
    assert body["message"] == ("Je ne sais pas encore ajouter cela à « Stock ». Je peux ajouter : le n° de lot et "
                               "la date de péremption.")


def test_should_name_created_module_when_request_is_unsupported_there():
    client, _, _ = eclipse_client(FakeModulesErp(names={"livraisons": "Livraisons"}))

    with client:
        body = propose(client, "livraisons", "Ajoute la météo", role="VENTE").json()

    assert body["message"] == ("Je ne sais pas encore ajouter cela à « Livraisons ». Vous pouvez me poser une "
                               "question sur ses données.")


def test_should_derive_module_name_from_slug_when_module_service_is_down():
    client, _, _ = eclipse_client(FakeModulesErp(down=True))

    with client:
        body = propose(client, "rendez-vous", "Ajoute la météo").json()

    assert body["message"].startswith("Je ne sais pas encore ajouter cela à « Rendez vous ».")


def test_should_ask_for_module_creation_when_user_wants_a_new_module():
    client, _, erp = eclipse_client()

    with client:
        body = propose(client, "accueil", "Crée un module pour suivre mes livraisons", role="VENTE").json()

    assert body == {"outcome": "CREATE_MODULE", "request": "Crée un module pour suivre mes livraisons",
                    "canApply": False}
    assert erp.requests == []


def test_should_say_it_cannot_remove_elements_when_user_asks_for_a_removal():
    client, _, erp = eclipse_client()

    with client:
        body = propose(client, "stock", "Supprime la colonne N° de lot").json()

    assert body["outcome"] == "UNSUPPORTED" and "adaptationKey" not in body
    assert body["message"] == ("Je ne sais pas encore retirer des éléments de « Stock ». Je peux ajouter : "
                               "le n° de lot et la date de péremption.")
    assert erp.requests == []


def test_should_say_it_cannot_remove_elements_from_home_when_typed_on_home():
    client, _, _ = eclipse_client()

    with client:
        body = propose(client, "accueil", "Enlève les avances sur salaire", role="RH").json()

    assert body["message"] == ("Je ne sais pas encore retirer des éléments depuis l’accueil. Je peux ajouter : "
                               "les avances sur salaire dans Équipe.")
