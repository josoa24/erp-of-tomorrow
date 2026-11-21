from dataclasses import dataclass
from typing import Protocol

import numpy as np


@dataclass(frozen=True)
class Transcription:
    text: str
    language: str
    duration_seconds: float


class AudioDecodingError(Exception):
    pass


class Transcriber(Protocol):
    def transcribe(self, audio: bytes, language: str) -> Transcription: ...


class SampleTranscriber(Protocol):
    def transcribe_samples(self, samples: np.ndarray, language: str, fast: bool) -> str: ...
