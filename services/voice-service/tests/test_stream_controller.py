import json
import logging

from fastapi.testclient import TestClient

from app.config import Settings
from app.main import create_app
from app.transcription.dependencies import get_settings, get_transcriber
from tests.audio_samples import silence, to_pcm, voice


class StubTranscriber:
    def __init__(self):
        self.languages = []

    def transcribe_samples(self, samples, language, fast):
        self.languages.append(language)
        return "Bonjour" if fast else "Bonjour, quel est notre chiffre d'affaires ?"


def client_with(transcriber):
    app = create_app()
    app.dependency_overrides[get_transcriber] = lambda: transcriber
    app.dependency_overrides[get_settings] = lambda: Settings()
    return TestClient(app)


def test_should_stream_partial_then_final_text():
    client = client_with(StubTranscriber())

    with client.websocket_connect("/stream") as socket:
        socket.send_bytes(to_pcm(voice(1)))
        partial = socket.receive_json()
        socket.send_bytes(to_pcm(silence(0.8)))
        processing = socket.receive_json()
        final = socket.receive_json()

    assert partial == {"type": "partial", "text": "Bonjour"}
    assert processing == {"type": "processing"}
    assert final == {"type": "final", "text": "Bonjour, quel est notre chiffre d'affaires ?"}


def test_should_finalize_on_stop_message_with_requested_language():
    transcriber = StubTranscriber()
    client = client_with(transcriber)

    with client.websocket_connect("/stream?language=mg") as socket:
        socket.send_bytes(to_pcm(voice(0.3)))
        socket.send_text("stop")
        events = [socket.receive_json() for _ in range(3)]

    assert [event["type"] for event in events] == ["processing", "final", "done"]
    assert transcriber.languages == ["mg"]


def test_should_serve_demo_page():
    response = TestClient(create_app()).get("/demo")

    assert response.status_code == 200
    assert "Transcription en direct" in response.text


def test_should_log_browser_debug_messages(caplog):
    caplog.set_level(logging.INFO, logger="voice.stream")
    client = client_with(StubTranscriber())

    with client.websocket_connect("/stream") as socket:
        socket.send_text(json.dumps({"type": "log", "message": "micro : « Realtek »"}))
        socket.send_text("pas du json")
        socket.send_text("stop")
        socket.receive_json()

    assert "navigateur : micro : « Realtek »" in caplog.text
