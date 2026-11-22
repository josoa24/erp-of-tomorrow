"""Limite de débit par utilisateur (fenêtre glissante) et consignes de langue / devise du prompt."""
from datetime import date

from app.agent.prompts import build_system_instruction
from app.api.rate_limit import SlidingWindowLimiter
from tests.fake_gemini import text_response
from tests.helpers import gateway_headers, make_app


def test_sliding_window():
    now = [0.0]
    limiter = SlidingWindowLimiter(2, window_s=60, clock=lambda: now[0])
    assert limiter.hit("u1") == 0 and limiter.hit("u1") == 0
    assert limiter.hit("u1") == 60 and limiter.hit("u2") == 0   # par utilisateur
    now[0] = 30
    assert limiter.hit("u1") == 30
    now[0] = 60
    assert limiter.hit("u1") == 0   # la première requête est sortie de la fenêtre


def test_chat_returns_429_beyond_limit():
    c, _, _ = make_app([text_response("a"), text_response("b")], ai_rate_limit_per_minute=2)
    with c:
        for _ in range(2):
            assert c.post("/agent/chat", json={"message": "x"}, headers=gateway_headers("VENTE", "5")).status_code == 200
        r = c.post("/agent/chat/stream", json={"message": "x"}, headers=gateway_headers("VENTE", "5"))
        assert r.status_code == 429 and r.json()["type"] == "RATE_LIMIT" and int(r.headers["Retry-After"]) > 0
        assert "réessayez" in r.json()["message"]
        assert c.get("/tools", headers=gateway_headers("VENTE", "5")).status_code == 200  # pas d'appel Gemini


def test_prompt_language_and_ariary():
    text = build_system_instruction(today=date(2026, 11, 22))
    assert "malgache" in text and "anglais" in text and "français" in text
    assert "Ariary (Ar)" in text and "BLOCKED" in text
