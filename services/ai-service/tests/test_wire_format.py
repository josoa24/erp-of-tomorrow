"""Test d'intégration avec le VRAI SDK google-genai contre un faux serveur
HTTP local qui imite l'API REST Gemini. Vérifie le JSON réellement émis :
system_instruction, tools, toolConfig, functionResponse.id, thoughtSignature.
"""
import asyncio
import base64
import json
import os
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer

from google import genai
from google.genai import types

from app.agent.orchestrator import GeminiAgent
from app.config import Settings
from app.erp.client import ErpClient
from app.tools.registry import ToolRegistry
from tests.fake_erp import FakeErp
from tests.helpers import as_user

# Le faux serveur est local : ne pas passer par un éventuel proxy système.
for _var in ("NO_PROXY", "no_proxy"):
    os.environ[_var] = ",".join(filter(None, [os.environ.get(_var), "127.0.0.1", "localhost"]))

SIG = base64.b64encode(b"opaque-signature").decode()

SCRIPT = [
    {"candidates": [{"content": {"role": "model", "parts": [
        {"functionCall": {"id": "fc1", "name": "get_stock_summary", "args": {}}, "thoughtSignature": SIG},
        {"functionCall": {"id": "fc2", "name": "get_stock_status", "args": {"risk": "CRITICAL"}}},
    ]}, "finishReason": "STOP"}], "usageMetadata": {"promptTokenCount": 300, "candidatesTokenCount": 20}},
    {"candidates": [{"content": {"role": "model", "parts": [{"text": "Stock : 2 550 000. Le riz est critique."}]},
                     "finishReason": "STOP"}], "usageMetadata": {"promptTokenCount": 500, "candidatesTokenCount": 15}},
]


def test_real_sdk_wire_format():
    received = []
    script = list(SCRIPT)

    class Handler(BaseHTTPRequestHandler):
        def do_POST(self):
            body = json.loads(self.rfile.read(int(self.headers["Content-Length"])))
            received.append({"path": self.path, "body": body, "key": self.headers.get("x-goog-api-key")})
            payload = json.dumps(script.pop(0)).encode()
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.send_header("Content-Length", str(len(payload)))
            self.end_headers()
            self.wfile.write(payload)

        def log_message(self, *a):
            pass

    server = HTTPServer(("127.0.0.1", 0), Handler)
    threading.Thread(target=server.serve_forever, daemon=True).start()
    try:
        client = genai.Client(api_key="test-key", http_options=types.HttpOptions(
            base_url=f"http://127.0.0.1:{server.server_port}", timeout=5000))
        settings = Settings(gemini_model="gemini-test")
        agent = GeminiAgent(client, ToolRegistry(ErpClient(FakeErp().http())), settings)
        with as_user("STOCK"):
            result = asyncio.run(agent.run("Valeur du stock et produits critiques ?"))
    finally:
        server.shutdown()

    assert result.response == "Stock : 2 550 000. Le riz est critique."
    assert [c.name for c in result.tool_calls] == ["get_stock_summary", "get_stock_status"]
    assert len(received) == 2
    assert "models/gemini-test:generateContent" in received[0]["path"]

    first = received[0]["body"]
    assert first["systemInstruction"]["parts"][0]["text"].startswith("Tu es l'assistant de l'ERP")
    assert len(first["tools"][0]["functionDeclarations"]) == 13  # outils du rôle STOCK
    assert "Date du jour" in first["systemInstruction"]["parts"][0]["text"]
    assert first["toolConfig"]["functionCallingConfig"]["mode"] == "AUTO"

    second = received[1]["body"]["contents"]
    assert [c["role"] for c in second] == ["user", "model", "user"]
    assert second[1]["parts"][0]["thoughtSignature"] == SIG  # signature renvoyée intacte
    frs = [p["functionResponse"] for p in second[2]["parts"]]
    assert [(f["id"], f["name"]) for f in frs] == [("fc1", "get_stock_summary"), ("fc2", "get_stock_status")]
    assert frs[1]["response"]["data"][0]["risk"] == "CRITICAL"
    # La clé ne transite que dans l'en-tête, jamais dans le corps
    assert received[0]["key"] == "test-key"
    assert "test-key" not in json.dumps(received[0]["body"])
