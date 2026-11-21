import numpy as np

from app.streaming.pcm import SAMPLE_RATE, rms

FRAME_SAMPLES = 480
LEADING_AUDIO_SECONDS = 0.3


class UtteranceBuffer:
    def __init__(
        self,
        speech_threshold: float = 0.01,
        silence_seconds: float = 0.7,
        partial_interval_seconds: float = 0.8,
        max_seconds: float = 15.0,
    ):
        self._speech_threshold = speech_threshold
        self._silence_samples = int(silence_seconds * SAMPLE_RATE)
        self._partial_samples = int(partial_interval_seconds * SAMPLE_RATE)
        self._max_samples = int(max_seconds * SAMPLE_RATE)
        self.reset()

    def reset(self) -> None:
        self._audio = np.zeros(0, dtype=np.float32)
        self._last_voice_end = None
        self._last_partial_end = 0

    def append(self, samples: np.ndarray) -> None:
        start = self._audio.size
        self._audio = np.concatenate([self._audio, samples])
        for offset in range(0, samples.size, FRAME_SAMPLES):
            frame = samples[offset : offset + FRAME_SAMPLES]
            if rms(frame) >= self._speech_threshold:
                self._last_voice_end = start + offset + frame.size
        if not self.has_speech:
            self._audio = self._audio[-int(LEADING_AUDIO_SECONDS * SAMPLE_RATE) :]

    @property
    def has_speech(self) -> bool:
        return self._last_voice_end is not None

    def is_complete(self) -> bool:
        if not self.has_speech:
            return False
        silence = self._audio.size - self._last_voice_end
        return silence >= self._silence_samples or self._audio.size >= self._max_samples

    def needs_partial(self) -> bool:
        return self.has_speech and self._audio.size - self._last_partial_end >= self._partial_samples

    def mark_partial(self) -> None:
        self._last_partial_end = self._audio.size

    def samples(self) -> np.ndarray:
        return self._audio
