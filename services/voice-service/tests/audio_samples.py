import numpy as np

from app.streaming.pcm import SAMPLE_RATE


def voice(seconds: float) -> np.ndarray:
    t = np.arange(int(seconds * SAMPLE_RATE)) / SAMPLE_RATE
    return (0.3 * np.sin(2 * np.pi * 220 * t)).astype(np.float32)


def silence(seconds: float) -> np.ndarray:
    return np.zeros(int(seconds * SAMPLE_RATE), dtype=np.float32)


def to_pcm(samples: np.ndarray) -> bytes:
    return (samples * 32767).astype("<i2").tobytes()
