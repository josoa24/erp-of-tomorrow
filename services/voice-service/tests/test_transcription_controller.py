import pytest
from fastapi.testclient import TestClient

from app.config import Settings
from app.main import create_app
from app.transcription.dependencies import get_settings, get_transcriber
from app.transcription.transcriber import AudioDecodingError, Transcription


class StubTranscriber:
    def __init__(self, error=None):
        self.error = error
        self.languages = []

    def transcribe(self, audio, language):
        self.languages.append(language)
        if self.error:
            raise self.error
        return Transcription(text="Quel est notre chiffre d'affaires ?", language=language, duration_seconds=2.1)


def client_with(transcriber):
    app = create_app()
    app.dependency_overrides[get_transcriber] = lambda: transcriber
    app.dependency_overrides[get_settings] = lambda: Settings(max_audio_bytes=100)
    return TestClient(app)


def post_audio(client, content=b"audio", content_type="audio/webm", data=None):
    return client.post("/transcribe", files={"audio": ("note.webm", content, content_type)}, data=data or {})


def test_should_return_transcribed_text():
    transcriber = StubTranscriber()

    response = post_audio(client_with(transcriber))

    assert response.status_code == 200
    assert response.json() == {"text": "Quel est notre chiffre d'affaires ?", "language": "fr", "durationSeconds": 2.1}
    assert transcriber.languages == ["fr"]


def test_should_use_requested_language():
    transcriber = StubTranscriber()

    post_audio(client_with(transcriber), data={"language": "mg"})

    assert transcriber.languages == ["mg"]


@pytest.mark.parametrize(
    "content,content_type,status",
    [(b"", "audio/webm", 400), (b"x" * 101, "audio/webm", 413), (b"audio", "text/plain", 415)],
)
def test_should_reject_invalid_audio(content, content_type, status):
    transcriber = StubTranscriber()

    response = post_audio(client_with(transcriber), content, content_type)

    assert response.status_code == status
    assert response.json()["type"] == "VALIDATION"
    assert transcriber.languages == []


def test_should_return_422_when_audio_cannot_be_decoded():
    response = post_audio(client_with(StubTranscriber(error=AudioDecodingError("Invalid data"))))

    assert response.status_code == 422
    assert response.json()["type"] == "VALIDATION"


def test_should_report_health():
    response = TestClient(create_app()).get("/health")

    assert response.json() == {"status": "UP"}
