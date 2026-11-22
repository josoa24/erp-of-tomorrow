import asyncio
import time
from collections.abc import Awaitable, Callable

from fastapi.concurrency import run_in_threadpool

from app.streaming.pcm import SAMPLE_RATE, to_float32
from app.streaming.utterance_buffer import UtteranceBuffer
from app.streaming.utterance_logger import UtteranceLogger
from app.transcription.transcriber import SampleTranscriber

Sender = Callable[[dict], Awaitable[None]]


class StreamSession:
    def __init__(
        self,
        transcriber: SampleTranscriber,
        language: str,
        buffer: UtteranceBuffer,
        send: Sender,
        utterance_logger: UtteranceLogger | None = None,
        session_id: str = "-",
    ):
        self._transcriber = transcriber
        self._language = language
        self._buffer = buffer
        self._send = send
        self._log = utterance_logger or UtteranceLogger()
        self._session_id = session_id
        self._utterance = 0
        self._partial_task: asyncio.Task | None = None
        self._received_samples = 0
        self._peak = 0.0

    async def on_audio(self, chunk: bytes) -> None:
        samples = to_float32(chunk)
        self._received_samples += samples.size
        self._peak = max(self._peak, float(abs(samples).max(initial=0.0)))
        self._buffer.append(samples)
        if self._buffer.is_complete():
            await self._finalize()
        elif self._buffer.needs_partial() and not self._partial_running():
            self._buffer.mark_partial()
            self._partial_task = asyncio.create_task(self._partial(self._utterance, self._buffer.samples().copy()))

    async def finish(self) -> None:
        if self._buffer.has_speech:
            await self._finalize()
        await self._send({"type": "done"})

    async def close(self) -> None:
        if self._partial_running():
            self._partial_task.cancel()

    def audio_stats(self) -> tuple[float, float]:
        return self._received_samples / SAMPLE_RATE, self._peak

    def _partial_running(self) -> bool:
        return self._partial_task is not None and not self._partial_task.done()

    async def _partial(self, utterance: int, samples) -> None:
        text, elapsed = await self._transcribe(samples, fast=True)
        self._log.partial(self._session_id, utterance + 1, samples, text, elapsed)
        if text and utterance == self._utterance:
            await self._send({"type": "partial", "text": text})

    async def _finalize(self) -> None:
        samples = self._buffer.samples().copy()
        self._buffer.reset()
        self._utterance += 1
        await self._send({"type": "processing"})
        if self._partial_running():
            await self._partial_task
        text, elapsed = await self._transcribe(samples, fast=False)
        self._log.final(self._session_id, self._utterance, samples, text, elapsed, self._language)
        await self._send({"type": "final", "text": text})

    async def _transcribe(self, samples, fast: bool) -> tuple[str, float]:
        started = time.perf_counter()
        text = await run_in_threadpool(self._transcriber.transcribe_samples, samples, self._language, fast)
        return text, time.perf_counter() - started
