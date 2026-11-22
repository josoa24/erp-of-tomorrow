import json
import logging
import wave
from datetime import datetime
from pathlib import Path

import numpy as np

from app.streaming.pcm import SAMPLE_RATE, rms

logger = logging.getLogger("voice.stream")


class UtteranceLogger:
    def __init__(self, debug_dir: str = ""):
        self._debug_dir = Path(debug_dir) if debug_dir else None
        if self._debug_dir:
            self._debug_dir.mkdir(parents=True, exist_ok=True)

    def partial(self, session: str, utterance: int, samples: np.ndarray, text: str, elapsed: float) -> None:
        logger.info(
            "[%s #%d] provisoire  audio=%.1fs calcul=%.2fs texte=%r",
            session, utterance, samples.size / SAMPLE_RATE, elapsed, text,
        )

    def final(self, session: str, utterance: int, samples: np.ndarray, text: str, elapsed: float, language: str) -> None:
        level = rms(samples)
        logger.info(
            "[%s #%d] definitif   audio=%.1fs niveau=%.3f calcul=%.2fs langue=%s texte=%r",
            session, utterance, samples.size / SAMPLE_RATE, level, elapsed, language, text,
        )
        if self._debug_dir:
            self._save(session, utterance, samples, text, elapsed, language, level)

    def _save(self, session, utterance, samples, text, elapsed, language, level) -> None:
        now = datetime.now()
        audio_file = self._debug_dir / f"{now:%Y%m%d-%H%M%S}-{session}-{utterance}.wav"
        with wave.open(str(audio_file), "wb") as file:
            file.setnchannels(1)
            file.setsampwidth(2)
            file.setframerate(SAMPLE_RATE)
            file.writeframes((np.clip(samples, -1, 1) * 32767).astype("<i2").tobytes())
        entry = {
            "at": now.isoformat(timespec="seconds"),
            "file": audio_file.name,
            "language": language,
            "text": text,
            "audioSeconds": round(samples.size / SAMPLE_RATE, 2),
            "level": round(level, 4),
            "elapsedSeconds": round(elapsed, 2),
        }
        with open(self._debug_dir / "transcriptions.jsonl", "a", encoding="utf-8") as journal:
            journal.write(json.dumps(entry, ensure_ascii=False) + "\n")
