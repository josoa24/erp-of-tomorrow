from pydantic import BaseModel

from app.transcription.transcriber import Transcription


class TranscriptionResponse(BaseModel):
    text: str
    language: str
    durationSeconds: float

    @classmethod
    def of(cls, transcription: Transcription) -> "TranscriptionResponse":
        return cls(
            text=transcription.text,
            language=transcription.language,
            durationSeconds=transcription.duration_seconds,
        )
