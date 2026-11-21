from types import SimpleNamespace

import pytest

from app.config import Settings
from app.transcription.transcriber import AudioDecodingError
from app.transcription.whisper_transcriber import WhisperTranscriber


class FakeModel:
    def __init__(self, error=None):
        self.error = error
        self.calls = []

    def transcribe(self, audio, language, vad_filter):
        self.calls.append((audio.read(), language, vad_filter))
        if self.error:
            raise self.error
        segments = [SimpleNamespace(text=" Quel est notre "), SimpleNamespace(text="chiffre d'affaires ? ")]
        return iter(segments), SimpleNamespace(language=language, duration=2.345)


def build(model):
    factory_calls = []

    def factory(settings):
        factory_calls.append(settings)
        return model

    return WhisperTranscriber(Settings(), factory), factory_calls


def test_should_join_segments_into_text():
    model = FakeModel()
    transcriber, _ = build(model)

    result = transcriber.transcribe(b"audio", "fr")

    assert result.text == "Quel est notre chiffre d'affaires ?"
    assert result.language == "fr"
    assert result.duration_seconds == 2.35
    assert model.calls == [(b"audio", "fr", True)]


def test_should_load_model_only_once():
    transcriber, factory_calls = build(FakeModel())

    transcriber.transcribe(b"a", "fr")
    transcriber.transcribe(b"b", "fr")

    assert len(factory_calls) == 1


def test_should_wrap_decoding_failure():
    transcriber, _ = build(FakeModel(error=ValueError("Invalid data")))

    with pytest.raises(AudioDecodingError):
        transcriber.transcribe(b"not audio", "fr")
