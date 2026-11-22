import os
from dataclasses import dataclass

DEFAULT_PROMPT = (
    "Bonjour, est-ce que tu m'entends ? Quel est notre chiffre d'affaires ce mois-ci ? "
    "Ventes, stock, produits, clients, fournisseurs, employés, congés, bon de commande."
)


@dataclass(frozen=True)
class Settings:
    model_size: str = "small"
    partial_model_size: str = ""
    device: str = "cpu"
    compute_type: str = "int8"
    language: str = "fr"
    initial_prompt: str = DEFAULT_PROMPT
    max_audio_bytes: int = 10 * 1024 * 1024
    debug_dir: str = ""


def load_settings() -> Settings:
    return Settings(
        model_size=os.getenv("VOICE_MODEL_SIZE", Settings.model_size),
        partial_model_size=os.getenv("VOICE_PARTIAL_MODEL_SIZE", Settings.partial_model_size),
        device=os.getenv("VOICE_DEVICE", Settings.device),
        compute_type=os.getenv("VOICE_COMPUTE_TYPE", Settings.compute_type),
        language=os.getenv("VOICE_LANGUAGE", Settings.language),
        initial_prompt=os.getenv("VOICE_INITIAL_PROMPT", Settings.initial_prompt),
        max_audio_bytes=int(os.getenv("VOICE_MAX_AUDIO_BYTES", Settings.max_audio_bytes)),
        debug_dir=os.getenv("VOICE_DEBUG_DIR", Settings.debug_dir),
    )
