from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class Transcription:
    text: str
    language: str
    duration_seconds: float


class AudioDecodingError(Exception):
    pass


class Transcriber(Protocol):
    def transcribe(self, audio: bytes, language: str) -> Transcription: ...
