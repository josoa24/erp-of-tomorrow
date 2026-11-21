import json
import logging
import wave

import numpy as np

from app.streaming.utterance_logger import UtteranceLogger
from tests.audio_samples import voice


def test_should_log_partial_and_final_without_saving_by_default(tmp_path, caplog):
    caplog.set_level(logging.INFO, logger="voice.stream")
    utterance_logger = UtteranceLogger()

    utterance_logger.partial("abc123", 1, voice(1), "Bonjour", 0.4)
    utterance_logger.final("abc123", 1, voice(1), "Bonjour !", 1.2, "fr")

    assert "[abc123 #1] provisoire" in caplog.text
    assert "texte='Bonjour !'" in caplog.text
    assert list(tmp_path.iterdir()) == []


def test_should_save_wav_and_journal_in_debug_dir(tmp_path):
    utterance_logger = UtteranceLogger(str(tmp_path / "debug"))

    utterance_logger.final("abc123", 2, voice(1.5), "Quel est notre chiffre d'affaires ?", 2.5, "fr")

    entry = json.loads((tmp_path / "debug" / "transcriptions.jsonl").read_text(encoding="utf-8"))
    assert entry["text"] == "Quel est notre chiffre d'affaires ?"
    assert entry["audioSeconds"] == 1.5
    assert entry["elapsedSeconds"] == 2.5
    assert entry["file"].endswith("-abc123-2.wav")
    with wave.open(str(tmp_path / "debug" / entry["file"]), "rb") as file:
        assert file.getframerate() == 16000
        assert file.getnframes() == 24000


def test_should_append_one_line_per_utterance(tmp_path):
    utterance_logger = UtteranceLogger(str(tmp_path))

    utterance_logger.final("s", 1, np.zeros(160, dtype=np.float32), "un", 0.1, "fr")
    utterance_logger.final("s", 2, np.zeros(160, dtype=np.float32), "deux", 0.1, "fr")

    lines = (tmp_path / "transcriptions.jsonl").read_text(encoding="utf-8").splitlines()
    assert [json.loads(line)["text"] for line in lines] == ["un", "deux"]
