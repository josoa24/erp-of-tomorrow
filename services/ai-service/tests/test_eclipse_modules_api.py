from tests.eclipse_support import eclipse_client, last_audit, propose_module

LIVRAISONS_NEED = "Suivre les livraisons de mes clients : quartier, livreur, heure prévue"


def test_should_propose_livraisons_template_when_description_mentions_deliveries():
    client, _, _ = eclipse_client()

    with client:
        body = propose_module(client, LIVRAISONS_NEED).json()

    assert body["source"] == "TEMPLATE" and body["canCreate"] is True
    assert body["proposal"]["name"] == "Livraisons" and body["proposal"]["icon"] == "truck"
    assert body["proposal"]["description"] == LIVRAISONS_NEED
    assert body["uncheckedColumns"] == ["signature_client"] and "hiddenColumns" not in body
    assert [c["key"] for c in body["proposal"]["columns"] if not c["listed"]] == [
        "vente_origine", "montant_a_encaisser", "signature_client"]
    assert body["accessText"] == "Toute l’équipe. Le gérant peut modifier ou supprimer le module."
    assert "note" not in body


def test_should_serialize_proposal_with_registry_camel_case_fields():
    client, _, _ = eclipse_client()

    with client:
        indicators = propose_module(client, LIVRAISONS_NEED).json()["proposal"]["indicators"]

    assert indicators[0] == {"key": "livrees_aujourdhui", "label": "Livrées aujourd’hui", "icon": "truck",
                             "kind": "RATIO_TODAY", "statuses": ["livree"], "todayColumn": "heure_prevue"}
    assert indicators[1]["detailColumn"] == "livreur" and indicators[2]["column"] == "montant_a_encaisser"


def test_should_not_allow_create_when_role_is_not_admin():
    client, _, _ = eclipse_client()

    with client:
        body = propose_module(client, LIVRAISONS_NEED, role="VENTE").json()

    assert body["canCreate"] is False and body["proposal"]["name"] == "Livraisons"


def test_should_return_generic_proposal_with_honest_note_when_no_template_and_no_key():
    client, _, _ = eclipse_client()

    with client:
        body = propose_module(client, "Je veux suivre les commandes spéciales de mes clients").json()

    assert body["source"] == "TEMPLATE" and body["proposal"]["name"] == "Commandes spéciales"
    assert [c["label"] for c in body["proposal"]["columns"]] == ["Nom", "Date", "Montant"]
    assert body["note"].startswith("Eclipse n’a pas de modèle pour ce besoin")
    assert body["uncheckedColumns"] == [] and all(c["listed"] for c in body["proposal"]["columns"])


def test_should_record_proposed_module_in_audit_when_a_proposal_is_returned():
    client, _, _ = eclipse_client()

    with client:
        propose_module(client, LIVRAISONS_NEED)
        entry = last_audit(client)

    assert entry["tools"] == ["eclipse_propose_module"] and entry["trust_label"] == "RECOMMENDATION"
    assert entry["actions"]["proposed"] == [{"tool": "eclipse_propose_module", "action_id": "livraisons"}]
