import asyncio
import json
import os
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer

from google import genai
from google.genai import types

from app.config import Settings
from app.eclipse.catalog import adaptation_scope
from app.eclipse.gemini_designer import ModuleDesigner
from app.eclipse.gemini_intent import IntentClassifier
from tests.eclipse_definitions import LOCATIONS

for _var in ("NO_PROXY", "no_proxy"):
    os.environ[_var] = ",".join(filter(None, [os.environ.get(_var), "127.0.0.1", "localhost"]))


def answer(text: str) -> dict:
    return {"candidates": [{"content": {"role": "model", "parts": [{"text": text}]}, "finishReason": "STOP"}]}


def run_against_fake_gemini(replies: list[dict], call):
    received: list[dict] = []

    class Handler(BaseHTTPRequestHandler):
        def do_POST(self):
            received.append(json.loads(self.rfile.read(int(self.headers["Content-Length"]))))
            payload = json.dumps(replies.pop(0)).encode()
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.send_header("Content-Length", str(len(payload)))
            self.end_headers()
            self.wfile.write(payload)

        def log_message(self, *args):
            pass

    server = HTTPServer(("127.0.0.1", 0), Handler)
    threading.Thread(target=server.serve_forever, daemon=True).start()
    try:
        client = genai.Client(api_key="test-key", http_options=types.HttpOptions(
            base_url=f"http://127.0.0.1:{server.server_port}", timeout=5000))
        result = asyncio.run(call(client, Settings(gemini_model="gemini-test")))
    finally:
        server.shutdown()
    return result, received


def test_should_send_structured_output_schema_when_classifying_with_real_sdk():
    verdict = json.dumps({"intent": "ADAPTATION", "adaptationKey": "stock.lots-peremption"})

    async def call(client, settings):
        return await IntentClassifier(client, settings).classify("Prévenir avant la fin des yaourts", "Stock",
                                                                 adaptation_scope("stock", "ADMIN"))

    result, received = run_against_fake_gemini([answer(verdict)], call)

    generation = received[0]["generationConfig"]
    assert result.adaptation_key == "stock.lots-peremption"
    assert generation["temperature"] == 0 and generation["responseMimeType"] == "application/json"
    assert generation["responseJsonSchema"]["properties"]["intent"]["enum"][0] == "ADAPTATION"


def test_should_send_module_definition_schema_when_designing_with_real_sdk():
    async def call(client, settings):
        return await ModuleDesigner(client, settings).design("Suivre la location de matériel")

    result, received = run_against_fake_gemini([answer(json.dumps(LOCATIONS))], call)

    schema = received[0]["generationConfig"]["responseJsonSchema"]
    assert result.name == "Locations"
    assert {"ColumnSpec", "StatusSpec", "IndicatorSpec"} <= set(schema["$defs"])
    assert "todayColumn" in schema["$defs"]["IndicatorSpec"]["properties"]
