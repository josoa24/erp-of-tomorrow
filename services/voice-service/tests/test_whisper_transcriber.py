from types import SimpleNamespace

import numpy as np

from app.config import Settings
from app.transcription.whisper_transcriber import WhisperTranscriber

DECODED = np.zeros(37600, dtype=np.float32)


class FakeModel:
    def __init__(self, name="base"):
        self.name = name
        self.calls = []

    def transcribe(self, audio, language, **options):
        self.calls.append((audio, language, options))
        segments = [SimpleNamespace(text=" Quel est notre "), SimpleNamespace(text="chiffre d'affaires ? ")]
        return iter(segments), SimpleNamespace(language=language)


def build(model, settings=Settings(model_size="base", language="en")):
    factory_calls = []

    def factory(_, size):
        factory_calls.append(size)
        return model if size == settings.model_size else FakeModel(size)

    return WhisperTranscriber(settings, factory, decoder=lambda content: DECODED), factory_calls


def test_should_decode_then_join_segments_into_text():
    model = FakeModel()
    transcriber, _ = build(model)

    result = transcriber.transcribe(b"audio", "fr")

    assert result.text == "Quel est notre chiffre d'affaires ?"
    assert result.language == "fr"
    assert result.duration_seconds == 2.35
    assert model.calls[0][0] is DECODED
    assert model.calls[0][2] == {"vad_filter": True, "initial_prompt": None}


def test_should_load_model_only_once():
    transcriber, factory_calls = build(FakeModel())

    transcriber.transcribe(b"a", "fr")
    transcriber.transcribe(b"b", "fr")

    assert len(factory_calls) == 1


def test_should_transcribe_samples_fast_with_greedy_search():
    model = FakeModel()
    transcriber, _ = build(model)

    text = transcriber.transcribe_samples(np.zeros(16000, dtype=np.float32), "fr", fast=True)

    assert text == "Quel est notre chiffre d'affaires ?"
    assert model.calls[0][2]["beam_size"] == 1
    assert model.calls[0][2]["vad_filter"] is True


def test_should_use_beam_search_for_final_samples():
    model = FakeModel()
    transcriber, _ = build(model)

    transcriber.transcribe_samples(np.zeros(160, dtype=np.float32), "fr", fast=False)

    assert model.calls[0][2]["beam_size"] == 5


def test_should_use_partial_model_only_for_fast_transcriptions():
    main_model = FakeModel()
    transcriber, factory_calls = build(main_model, Settings(model_size="base", partial_model_size="tiny"))

    transcriber.transcribe_samples(DECODED, "fr", fast=True)
    transcriber.transcribe_samples(DECODED, "fr", fast=False)

    assert factory_calls == ["tiny", "base"]
    assert len(main_model.calls) == 1
    assert main_model.calls[0][2]["beam_size"] == 5


def test_should_send_initial_prompt_only_for_default_language():
    model = FakeModel()
    transcriber, _ = build(model, Settings(model_size="base", language="fr", initial_prompt="Chiffre d'affaires"))

    transcriber.transcribe_samples(DECODED, "fr", fast=True)
    transcriber.transcribe_samples(DECODED, "mg", fast=True)

    assert model.calls[0][2]["initial_prompt"] == "Chiffre d'affaires"
    assert model.calls[1][2]["initial_prompt"] is None
