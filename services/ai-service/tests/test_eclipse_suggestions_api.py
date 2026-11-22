from tests.eclipse_support import eclipse_client
from tests.fake_modules_erp import FakeModulesErp
from tests.helpers import gateway_headers


def suggestions(client, role="ADMIN"):
    return client.get("/eclipse/suggestions", headers=gateway_headers(role))


def test_should_list_inactive_adaptations_when_admin_asks_for_suggestions():
    client, _, _ = eclipse_client(FakeModulesErp(active=("ventes.mobile-money-credit",)))

    with client:
        body = suggestions(client).json()

    assert [s["adaptationKey"] for s in body] == ["stock.lots-peremption", "relations.nif-plafond-credit",
                                                  "equipe.avances-salaire"]
    assert body[0] == {"adaptationKey": "stock.lots-peremption", "moduleKey": "stock",
                       "title": "Eclipse suggère un champ « N° de lot » dans Stock",
                       "reason": "Vous suivez des produits périssables : Eclipse peut ajouter le n° de lot et "
                                 "la péremption."}


def test_should_filter_suggestions_when_role_accesses_a_single_module():
    client, _, _ = eclipse_client()

    with client:
        body = suggestions(client, role="RH").json()

    assert [(s["moduleKey"], s["adaptationKey"]) for s in body] == [("equipe", "equipe.avances-salaire")]


def test_should_return_no_suggestion_when_module_service_is_down():
    client, _, _ = eclipse_client(FakeModulesErp(down=True))

    with client:
        response = suggestions(client)

    assert response.status_code == 200 and response.json() == []


def test_should_return_no_suggestion_when_every_adaptation_is_active():
    client, _, _ = eclipse_client(FakeModulesErp(active=("stock.lots-peremption",)))

    with client:
        body = suggestions(client, role="STOCK").json()

    assert body == []
