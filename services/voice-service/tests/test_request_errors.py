from fastapi.testclient import TestClient

from app.main import create_app
from app.transcription.dependencies import get_transcriber
from app.transcription.transcriber import AudioDecodingError
from tests.test_transcription_controller import StubTranscriber, client_with, post_audio


def test_should_answer_in_shared_error_format_when_audio_field_is_missing():
    response = TestClient(create_app()).post("/transcribe", data={"language": "fr"})

    body = response.json()
    assert response.status_code == 400
    assert (body["status"], body["type"]) == (400, "VALIDATION")
    assert "audio" in body["message"] and "timestamp" in body


def test_should_hide_decoder_details_when_audio_cannot_be_decoded():
    error = AudioDecodingError("[Errno 1094995529] Invalid data found when processing input")

    response = post_audio(client_with(StubTranscriber(error=error)))

    assert response.status_code == 422
    assert "Errno" not in response.json()["message"]
    assert response.json()["message"].startswith("Audio illisible")


def test_should_not_load_any_model_when_app_is_created_without_preload():
    app = create_app()
    calls = []
    app.dependency_overrides[get_transcriber] = lambda: calls.append("chargé")

    with TestClient(app) as client:
        client.get("/health")

    assert calls == []
