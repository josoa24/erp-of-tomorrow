from functools import lru_cache

from app.config import Settings, load_settings
from app.streaming.utterance_logger import UtteranceLogger
from app.transcription.transcriber import Transcriber
from app.transcription.whisper_transcriber import WhisperTranscriber


@lru_cache
def get_settings() -> Settings:
    return load_settings()


@lru_cache
def get_transcriber() -> Transcriber:
    return WhisperTranscriber(get_settings())


@lru_cache
def get_utterance_logger() -> UtteranceLogger:
    return UtteranceLogger(get_settings().debug_dir)
