import numpy as np

SAMPLE_RATE = 16000


def to_float32(chunk: bytes) -> np.ndarray:
    usable = chunk[: len(chunk) - len(chunk) % 2]
    return np.frombuffer(usable, dtype="<i2").astype(np.float32) / 32768.0


def rms(samples: np.ndarray) -> float:
    if samples.size == 0:
        return 0.0
    return float(np.sqrt(np.mean(np.square(samples))))
