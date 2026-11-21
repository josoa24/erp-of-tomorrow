import asyncio

from app.agent.memory import ConversationStore
from tests.fake_gemini import fc_response, text_response
from tests.helpers import gateway_headers, make_app


def texts(contents):
    return [(c.role, "".join(p.text or "" for p in c.parts if p.text)) for c in contents]


# ------------------------------- Store -------------------------------------- #
def test_store_keeps_only_last_turns_and_truncates():
    store = ConversationStore(max_turns=2, max_chars=10)
    conv, existed = store.get_or_create("c1")
    assert not existed
    for i in range(4):
        store.append_exchange(conv, f"q{i}", f"réponse très longue {i}")
    assert conv.turns == 2
    assert texts(conv.messages) == [("user", "q2"), ("model", "réponse tr […]"),
                                    ("user", "q3"), ("model", "réponse tr […]")]
    assert store.get_or_create("c1")[1] is True


def test_store_ttl_and_capacity():
    now = [0.0]
    store = ConversationStore(ttl_s=60, max_conversations=2, clock=lambda: now[0])
    store.get_or_create("a")
    now[0] = 30
    store.get_or_create("b")
    now[0] = 70  # "a" a expiré (inactif depuis 70 s), "b" non
    assert store.get("a") is None and store.get("b") is not None
    store.get_or_create("c")
    store.get_or_create("d")  # capacité 2 : le plus ancien ("b") est évincé
    assert len(store) == 2 and store.get("b") is None
    assert store.delete("c") and not store.delete("c")


# -------------------------------- API --------------------------------------- #
def test_api_conversation_flow():
    c, fake, _ = make_app([
        fc_response(("get_stock_status", {})), text_response("Le riz est critique."),
        fc_response(("get_product_autonomy", {"product_id": 1})), text_response("2 jours."),
    ])
    with c:
        h = gateway_headers("STOCK")
        r1 = c.post("/agent/chat", json={"message": "Quels produits risquent la rupture ?"}, headers=h).json()
        cid = r1["conversation_id"]
        r2 = c.post("/agent/chat", json={"message": "Combien de jours pour ce produit ?", "conversation_id": cid},
                    headers=h).json()
        assert r2["history_turns"] == 2
        assert texts(fake.requests[2]["contents"])[:3] == [
            ("user", "Quels produits risquent la rupture ?"), ("model", "Le riz est critique."),
            ("user", "Combien de jours pour ce produit ?")]
        assert c.get(f"/agent/conversations/{cid}", headers=h).json()["turns"] == 2
        assert c.get(f"/agent/conversations/{cid}", headers=gateway_headers("STOCK", "2")).status_code == 404
        assert c.delete(f"/agent/conversations/{cid}", headers=h).json()["deleted"] is True


def test_failed_request_is_not_stored():
    c, _, _ = make_app([TimeoutError()])
    with c:
        h = gateway_headers()
        assert c.post("/agent/chat", json={"message": "x", "conversation_id": "conv1"}, headers=h).status_code == 504
        assert c.get("/agent/conversations/conv1", headers=h).json()["turns"] == 0
