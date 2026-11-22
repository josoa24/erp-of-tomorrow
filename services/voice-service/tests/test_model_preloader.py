import logging

from app.config import Settings
from app.transcription.model_preloader import preload_models, start_preload
from tests.test_whisper_transcriber import FakeModel, build


def test_should_load_final_and_partial_models_once_each():
    transcriber, factory_calls = build(FakeModel(), Settings(model_size="small", partial_model_size="tiny"))

    assert transcriber.preload() == ["small", "tiny"]
    transcriber.preload()

    assert factory_calls == ["small", "tiny"]


def test_should_load_a_single_model_when_partial_size_is_empty_or_identical():
    transcriber, factory_calls = build(FakeModel(), Settings(model_size="small", partial_model_size="small"))

    assert transcriber.preload() == ["small"]
    assert factory_calls == ["small"]


def test_should_log_and_keep_running_when_preload_fails(caplog):
    class Broken:
        def preload(self):
            raise OSError("téléchargement interrompu")

    with caplog.at_level(logging.ERROR, logger="voice.preload"):
        preload_models(Broken())

    assert "retenté à la première transcription" in caplog.text


def test_should_preload_in_a_background_thread():
    transcriber, factory_calls = build(FakeModel(), Settings(model_size="base"))

    start_preload(transcriber).join(timeout=5)

    assert factory_calls == ["base"]
