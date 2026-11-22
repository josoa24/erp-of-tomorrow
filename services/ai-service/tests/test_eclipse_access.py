from tests.eclipse_sentences import STOCK_SENTENCE
from tests.eclipse_support import eclipse_client, propose, propose_module


def test_should_refuse_with_forbidden_when_role_cannot_access_the_module():
    client, _, erp = eclipse_client()

    with client:
        response = propose(client, "stock", STOCK_SENTENCE, role="VENTE")

    assert response.status_code == 403 and response.json()["type"] == "FORBIDDEN"
    assert response.json()["message"] == "Le rôle VENTE n’a pas accès au module « Stock »."
    assert erp.requests == []


def test_should_offer_only_accessible_adaptations_when_role_types_on_home():
    client, _, _ = eclipse_client()

    with client:
        body = propose(client, "accueil", STOCK_SENTENCE, role="VENTE").json()

    assert body["outcome"] == "UNSUPPORTED"
    assert body["message"] == ("Je ne sais pas encore faire cela depuis l’accueil. Je peux ajouter : le paiement "
                               "Mobile Money et le suivi du crédit dans Ventes.")


def test_should_require_gateway_identity_when_headers_are_missing():
    client, _, _ = eclipse_client()

    with client:
        responses = [client.post("/eclipse/adaptations/propose", json={"moduleKey": "stock", "request": "x"}),
                     client.post("/eclipse/modules/propose", json={"description": "x"}),
                     client.get("/eclipse/suggestions")]

    assert [r.status_code for r in responses] == [401, 401, 401]
    assert {r.json()["type"] for r in responses} == {"UNAUTHORIZED"}


def test_should_reject_request_when_body_breaks_validation():
    client, _, _ = eclipse_client()

    with client:
        responses = [propose(client, "Stock!", "Ajoute le lot"), propose(client, "stock", "   "),
                     propose(client, "stock", "x" * 501), propose_module(client, "")]

    assert [r.status_code for r in responses] == [400, 400, 400, 400]
    assert {r.json()["type"] for r in responses} == {"VALIDATION"}


def test_should_rate_limit_when_user_exceeds_the_limit():
    client, _, _ = eclipse_client(ai_rate_limit_per_minute=1)

    with client:
        first = propose(client, "stock", STOCK_SENTENCE)
        second = propose_module(client, "Suivre les livraisons")

    assert first.status_code == 200
    assert second.status_code == 429 and second.json()["type"] == "RATE_LIMIT"
    assert "Retry-After" in second.headers


def test_should_not_rate_limit_suggestions_when_limit_is_reached():
    client, _, _ = eclipse_client(ai_rate_limit_per_minute=1)

    with client:
        propose(client, "stock", STOCK_SENTENCE)
        response = client.get("/eclipse/suggestions", headers={"Authorization": "Bearer tok-ADMIN",
                                                                "X-User-Id": "1", "X-User-Role": "ADMIN"})

    assert response.status_code == 200
