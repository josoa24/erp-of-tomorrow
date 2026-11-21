from threading import Lock

import numpy as np

from app.config import Settings
from app.streaming.pcm import SAMPLE_RATE
from app.transcription.audio_decoder import decode_audio
from app.transcription.transcriber import Transcription


class WhisperTranscriber:
    def __init__(self, settings: Settings, model_factory=None, decoder=decode_audio):
        self._settings = settings
        self._model_factory = model_factory or _default_model_factory
        self._decoder = decoder
        self._models = {}
        self._lock = Lock()

    def transcribe(self, audio: bytes, language: str) -> Transcription:
        samples = self._decoder(audio)
        segments, info = self._model(self._settings.model_size).transcribe(
            samples, language=language, vad_filter=True, initial_prompt=self._prompt_for(language)
        )
        text = _join(segments)
        return Transcription(text=text, language=info.language, duration_seconds=round(samples.size / SAMPLE_RATE, 2))

    def transcribe_samples(self, samples: np.ndarray, language: str, fast: bool) -> str:
        size = (self._settings.partial_model_size or self._settings.model_size) if fast else self._settings.model_size
        segments, _ = self._model(size).transcribe(
            samples,
            language=language,
            beam_size=1 if fast else 5,
            vad_filter=True,
            condition_on_previous_text=False,
            without_timestamps=True,
            initial_prompt=self._prompt_for(language),
        )
        return _join(segments)

    def _prompt_for(self, language: str) -> str | None:
        if language != self._settings.language or not self._settings.initial_prompt:
            return None
        return self._settings.initial_prompt

    def _model(self, size: str):
        with self._lock:
            if size not in self._models:
                self._models[size] = self._model_factory(self._settings, size)
            return self._models[size]


def _join(segments) -> str:
    return " ".join(segment.text.strip() for segment in segments).strip()


def _default_model_factory(settings: Settings, size: str):
    from faster_whisper import WhisperModel

    return WhisperModel(size, device=settings.device, compute_type=settings.compute_type)
