import json

from tests.fake_gemini import fc_response, text_chunks
from tests.helpers import gateway_headers, make_app


def sse(text):
    return [(b.split("\n")[0][7:], json.loads(b.split("\n")[1][6:])) for b in text.strip().split("\n\n")]


def test_stream_events_and_context():
    c, fake, erp = make_app([
        fc_response(("get_sales_summary", {"period": "today"})), text_chunks("CA du jour : ", "**310 000**."),
        fc_response(("get_sales_summary", {"period": "yesterday"})), text_chunks("Hier : 310 000."),
    ])
    with c:
        r = c.post("/agent/chat/stream", json={"message": "CA du jour ?"}, headers=gateway_headers("VENTE"))
        assert r.headers["content-type"].startswith("text/event-stream")
        evs = sse(r.text)
        assert [e for e, _ in evs] == ["start", "tool_call", "tool_result", "token", "token", "done"]
        cid = evs[0][1]["conversation_id"]
        evs2 = sse(c.post("/agent/chat/stream", json={"message": "Et hier ?", "conversation_id": cid},
                          headers=gateway_headers("VENTE")).text)
        assert evs2[-1][1]["history_turns"] == 2
        assert fake.requests[2]["contents"][1].parts[0].text == "CA du jour : **310 000**."
        # même id, autre utilisateur -> 404
        assert c.post("/agent/chat/stream", json={"message": "x", "conversation_id": cid},
                      headers=gateway_headers("VENTE", "2")).status_code == 404


def test_stream_error_event():
    c, _, _ = make_app([TimeoutError()])
    with c:
        evs = sse(c.post("/agent/chat/stream", json={"message": "x"}, headers=gateway_headers()).text)
    assert evs[-1][0] == "error" and evs[-1][1]["code"] == "GEMINI_TIMEOUT"
