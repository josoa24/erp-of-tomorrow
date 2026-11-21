import io
from threading import Lock

from app.config import Settings
from app.transcription.transcriber import AudioDecodingError, Transcription


class WhisperTranscriber:
    def __init__(self, settings: Settings, model_factory=None):
        self._settings = settings
        self._model_factory = model_factory or _default_model_factory
        self._model = None
        self._lock = Lock()

    def transcribe(self, audio: bytes, language: str) -> Transcription:
        try:
            segments, info = self._get_model().transcribe(io.BytesIO(audio), language=language, vad_filter=True)
            text = " ".join(segment.text.strip() for segment in segments).strip()
        except (ValueError, OSError) as error:
            raise AudioDecodingError(str(error)) from error
        return Transcription(text=text, language=info.language, duration_seconds=round(info.duration, 2))

    def _get_model(self):
        with self._lock:
            if self._model is None:
                self._model = self._model_factory(self._settings)
            return self._model


def _default_model_factory(settings: Settings):
    from faster_whisper import WhisperModel

    return WhisperModel(settings.model_size, device=settings.device, compute_type=settings.compute_type)
