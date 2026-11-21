"""Écritures : jamais sans confirmation explicite, toujours avec le token de l'utilisateur."""
import asyncio
import json

from app.agent.confirmation import ActionGuard
from app.erp.client import ErpClient
from app.tools.registry import ToolRegistry
from tests.fake_erp import FakeErp
from tests.fake_gemini import fc_response, text_chunks, text_response
from tests.helpers import as_user, gateway_headers, make_app


def run(c):
    return asyncio.run(c)


def test_guard_rules():
    now, pending = [0.0], {}
    g1 = ActionGuard(pending, current_turn=1, ttl_s=60, clock=lambda: now[0])
    p = g1.propose("record_stock_movement", {"product_id": 1, "type": "IN", "quantity": 50}, "Entrée")
    assert g1.take(p["action_id"], by_user=False)["error"]["code"] == "CONFIRMATION_NOT_GIVEN"
    g2 = ActionGuard(pending, current_turn=2, ttl_s=60, clock=lambda: now[0])
    assert g2.take(p["action_id"], by_user=False).tool == "record_stock_movement"
    assert g2.take(p["action_id"], by_user=False)["error"]["code"] == "ACTION_NOT_FOUND"
    p2 = g2.propose("create_sale", {}, "Vente")
    now[0] = 61
    assert g2.take(p2["action_id"], by_user=True)["error"]["code"] == "ACTION_NOT_FOUND"


def test_write_is_only_proposed_then_executed_next_turn():
    erp = FakeErp()
    reg, g = ToolRegistry(ErpClient(erp.http())), ActionGuard({}, current_turn=1)
    with as_user("STOCK"):
        p = run(reg.execute("record_stock_movement", {"product_id": 1, "type": "IN", "quantity": 50,
                                                      "reason": "Livraison"}, g))
        assert p["status"] == "CONFIRMATION_REQUIRED" and "+50" in p["summary"] and erp.writes() == []
        assert run(reg.execute("record_stock_movement", {"product_id": 1, "type": "IN", "quantity": 5}))[
            "error"]["code"] == "CONFIRMATION_UNAVAILABLE"
        g.current_turn = 2
        done = run(reg.execute("confirm_action", {"action_id": p["action_id"]}, g))
    assert done["success"] and done["stockAfter"] == 64 and erp.products[1]["quantity"] == 64
    w = erp.writes()[0]
    assert w.url.path == "/api/stock/products/1/movements" and w.headers["authorization"] == "Bearer tok-STOCK"
    assert json.loads(w.content) == {"type": "IN", "quantity": 50, "reason": "Livraison"}


def test_create_sale_creates_and_validates_after_confirmation():
    erp = FakeErp()
    reg, g = ToolRegistry(ErpClient(erp.http())), ActionGuard({}, current_turn=1)
    with as_user("ADMIN"):
        p = run(reg.execute("create_sale", {"lines": [{"product_id": 2, "quantity": 3}]}, g))
        g.current_turn = 2
        done = run(reg.execute("confirm_action", {"action_id": p["action_id"]}, g))
    assert done["status"] == "VALIDATED" and erp.products[2]["quantity"] == 57
    assert [r.url.path for r in erp.writes()] == ["/api/vente/sales", "/api/vente/sales/1/validate"]


def test_sale_validation_refused_keeps_draft():
    erp = FakeErp()
    reg, g = ToolRegistry(ErpClient(erp.http())), ActionGuard({}, current_turn=1)
    with as_user("ADMIN"):
        p = run(reg.execute("create_sale", {"lines": [{"product_id": 1, "quantity": 500}]}, g))
        g.current_turn = 2
        done = run(reg.execute("confirm_action", {"action_id": p["action_id"]}, g))
    assert done["error"]["code"] == "BUSINESS" and done["draft_sale_id"] == 1


def sse(text):
    return [(b.split("\n")[0][7:], json.loads(b.split("\n")[1][6:])) for b in text.strip().split("\n\n")]


def test_stream_propose_then_yes():
    c, fake, erp = make_app([
        fc_response(("search_products", {"search": "riz"})),
        fc_response(("record_stock_movement", {"product_id": 1, "type": "IN", "quantity": 50})),
        text_chunks("J'ajoute **50** sacs de Riz blanc (14 → 64). ", "Confirmez-vous ?"),
        "CONFIRM", text_chunks("C'est enregistré : 64 sacs."),
    ])
    with c:
        evs = sse(c.post("/agent/chat/stream", json={"message": "50 sacs de riz reçus"},
                         headers=gateway_headers("STOCK")).text)
        action = evs[-1][1]["pending_actions"][0]
        assert action["tool"] == "record_stock_movement" and erp.writes() == []
        fake.models_impl.script[0] = fc_response(("confirm_action", {"action_id": action["action_id"]}))
        evs2 = sse(c.post("/agent/chat/stream", json={"message": "oui", "conversation_id": evs[0][1]["conversation_id"]},
                          headers=gateway_headers("STOCK")).text)
    assert [p for e, p in evs2 if e == "tool_result"][0]["status"] == "executed"
    assert erp.products[1]["quantity"] == 64


def test_buttons_owner_and_role():
    c, _, erp = make_app([fc_response(("record_stock_movement", {"product_id": 1, "type": "OUT", "quantity": 4,
                                                                 "reason": "Casse"})), text_response("À confirmer.")])
    with c:
        r = c.post("/agent/chat", json={"message": "4 sacs de riz cassés"}, headers=gateway_headers("STOCK", "7")).json()
        cid, aid = r["conversation_id"], r["pending_actions"][0]["action_id"]
        # un autre utilisateur ne voit ni la conversation ni l'action
        assert c.post(f"/agent/conversations/{cid}/actions/{aid}/confirm",
                      headers=gateway_headers("ADMIN", "99")).status_code == 404
        ok = c.post(f"/agent/conversations/{cid}/actions/{aid}/confirm", headers=gateway_headers("STOCK", "7"))
        assert ok.status_code == 200 and ok.json()["stockAfter"] == 10
        assert c.post(f"/agent/conversations/{cid}/actions/{aid}/confirm",
                      headers=gateway_headers("STOCK", "7")).json()["type"] == "NOT_FOUND"
    assert len(erp.writes()) == 1


def test_button_reports_erp_business_error():
    c, _, erp = make_app([fc_response(("record_stock_movement", {"product_id": 1, "type": "OUT", "quantity": 999})),
                          text_response("?")])
    with c:
        r = c.post("/agent/chat", json={"message": "sortie 999"}, headers=gateway_headers("STOCK")).json()
        res = c.post(f"/agent/conversations/{r['conversation_id']}/actions/{r['pending_actions'][0]['action_id']}/confirm",
                     headers=gateway_headers("STOCK"))
    assert res.status_code == 422 and res.json()["type"] == "BUSINESS" and "Stock insuffisant" in res.json()["message"]
