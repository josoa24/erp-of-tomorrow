import numpy as np

from app.streaming.pcm import rms, to_float32


def test_should_convert_int16_little_endian_to_float():
    chunk = np.array([0, 16384, -32768], dtype="<i2").tobytes()

    samples = to_float32(chunk)

    assert samples.tolist() == [0.0, 0.5, -1.0]


def test_should_ignore_trailing_odd_byte():
    chunk = np.array([16384], dtype="<i2").tobytes() + b"\x01"

    assert to_float32(chunk).tolist() == [0.5]


def test_should_compute_rms():
    assert rms(np.array([0.5, -0.5], dtype=np.float32)) == 0.5
    assert rms(np.zeros(0, dtype=np.float32)) == 0.0
