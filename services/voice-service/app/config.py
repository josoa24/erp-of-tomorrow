import os
from dataclasses import dataclass


@dataclass(frozen=True)
class Settings:
    model_size: str = "small"
    device: str = "cpu"
    compute_type: str = "int8"
    language: str = "fr"
    max_audio_bytes: int = 10 * 1024 * 1024


def load_settings() -> Settings:
    return Settings(
        model_size=os.getenv("VOICE_MODEL_SIZE", Settings.model_size),
        device=os.getenv("VOICE_DEVICE", Settings.device),
        compute_type=os.getenv("VOICE_COMPUTE_TYPE", Settings.compute_type),
        language=os.getenv("VOICE_LANGUAGE", Settings.language),
        max_audio_bytes=int(os.getenv("VOICE_MAX_AUDIO_BYTES", Settings.max_audio_bytes)),
    )
