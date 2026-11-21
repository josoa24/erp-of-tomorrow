import io
import wave

import numpy as np
import pytest

from app.transcription.audio_decoder import decode_audio
from app.transcription.transcriber import AudioDecodingError


def wav_bytes(rate: int, channels: int, seconds: float) -> bytes:
    frames = (np.ones(int(rate * seconds) * channels) * 16384).astype("<i2").tobytes()
    buffer = io.BytesIO()
    with wave.open(buffer, "wb") as file:
        file.setnchannels(channels)
        file.setsampwidth(2)
        file.setframerate(rate)
        file.writeframes(frames)
    return buffer.getvalue()


def test_should_resample_stereo_44khz_to_mono_16khz():
    samples = decode_audio(wav_bytes(44100, 2, 1.0))

    assert samples.dtype == np.float32
    assert abs(samples.size - 16000) < 400
    assert abs(float(samples[8000]) - 0.5) < 0.01


def test_should_reject_non_audio_content():
    with pytest.raises(AudioDecodingError):
        decode_audio(b"ceci n'est pas un fichier audio")
