"""Journal des actions IA : contenu (sans donnée sensible), persistance SQLite, accès ADMIN."""
import json

from app.audit.store import AuditStore
from tests.fake_gemini import fc_response, text_response
from tests.helpers import gateway_headers, make_app
from tests.test_invoice_tools import INVOICE


def test_store_persists_in_sqlite_file(tmp_path):
    path = tmp_path / "sub" / "audit.db"
    store = AuditStore(str(path))
    store.record({"user_id": "7", "email": "a@b", "role": "STOCK", "conversation_id": "c1", "question": "q",
                  "tools": ["x"], "actions": {"proposed": []}, "trust_label": "FACT"})
    store.close()
    rows = AuditStore(str(path)).list(user_id="7")
    assert rows[0]["tools"] == ["x"] and rows[0]["created_at"].endswith("Z") and path.exists()


def test_chat_is_logged_without_token_results_or_bank_account():
    c, _, _ = make_app([fc_response(("search_suppliers", {"search": "riz"}),
                                    ("register_supplier_invoice", INVOICE)), text_response("Confirmez-vous ?")])
    with c:
        c.post("/agent/chat", json={"message": "Enregistre la facture " + "x" * 400},
               headers=gateway_headers("STOCK", "7"))
        r = c.get("/audit", params={"userId": "7"}, headers=gateway_headers("ADMIN", "1"))
    entry = r.json()["items"][0]
    assert r.status_code == 200 and r.json()["count"] == 1
    assert entry["user_id"] == "7" and entry["role"] == "STOCK" and entry["email"] == "stock@test.local"
    assert len(entry["question"]) == 300 and entry["tools"] == ["search_suppliers", "register_supplier_invoice"]
    assert entry["actions"]["proposed"][0]["tool"] == "register_supplier_invoice"
    assert entry["trust_label"] == "RECOMMENDATION"
    raw = json.dumps(entry)
    assert "tok-" not in raw and "MG46" not in raw and "1440000" not in raw


def test_button_confirmation_logged():
    c, _, _ = make_app([fc_response(("record_stock_movement", {"product_id": 1, "type": "IN", "quantity": 5})),
                        text_response("?")])
    with c:
        r = c.post("/agent/chat", json={"message": "5 sacs"}, headers=gateway_headers("STOCK", "7")).json()
        c.post(f"/agent/conversations/{r['conversation_id']}/actions/{r['pending_actions'][0]['action_id']}/confirm",
               headers=gateway_headers("STOCK", "7"))
        items = c.get("/audit", headers=gateway_headers("ADMIN")).json()["items"]
    assert items[0]["actions"]["confirmed"][0]["tool"] == "record_stock_movement"


def test_audit_reserved_to_admin():
    c, _, _ = make_app([])
    with c:
        r = c.get("/audit", headers=gateway_headers("STOCK"))
        assert r.status_code == 403 and r.json()["type"] == "FORBIDDEN" and "timestamp" in r.json()
        assert c.get("/audit").status_code == 401
        assert c.get("/audit", params={"limit": 0}, headers=gateway_headers("ADMIN")).status_code == 400
