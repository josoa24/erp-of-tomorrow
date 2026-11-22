import asyncio
import time

from app.streaming.stream_session import StreamSession
from app.streaming.utterance_buffer import UtteranceBuffer
from tests.audio_samples import silence, to_pcm, voice


class StubTranscriber:
    def __init__(self, text="Bonjour", partial_delay=0.0):
        self.text = text
        self.partial_delay = partial_delay
        self.calls = []

    def transcribe_samples(self, samples, language, fast):
        self.calls.append((samples.size, language, fast))
        if fast:
            time.sleep(self.partial_delay)
        return self.text


def run(transcriber, *chunks, stop=False):
    sent = []

    async def send(event):
        sent.append(event)

    async def feed():
        session = StreamSession(transcriber, "fr", UtteranceBuffer(partial_interval_seconds=0.8), send)
        for chunk in chunks:
            await session.on_audio(to_pcm(chunk))
            await asyncio.sleep(0.01)
        if stop:
            await session.finish()
        await asyncio.sleep(0.05)
        await session.close()

    asyncio.run(feed())
    return sent


def test_should_send_partial_while_speaking():
    transcriber = StubTranscriber()

    events = run(transcriber, voice(0.5), voice(0.5))

    assert events == [{"type": "partial", "text": "Bonjour"}]
    assert transcriber.calls[0][1:] == ("fr", True)


def test_should_send_final_after_silence():
    transcriber = StubTranscriber()

    events = run(transcriber, voice(0.5), silence(0.8), silence(1))

    assert events == [{"type": "processing"}, {"type": "final", "text": "Bonjour"}]
    assert transcriber.calls[-1][2] is False


def test_should_drop_partial_of_a_finished_utterance():
    transcriber = StubTranscriber(partial_delay=0.2)

    events = run(transcriber, voice(1), silence(0.8))

    assert events == [{"type": "processing"}, {"type": "final", "text": "Bonjour"}]
    assert [call[2] for call in transcriber.calls] == [True, False]


def test_should_finalize_on_stop_only_when_speech_was_heard():
    assert run(StubTranscriber(), silence(1), stop=True) == [{"type": "done"}]
    assert run(StubTranscriber(), voice(0.3), stop=True) == [
        {"type": "processing"},
        {"type": "final", "text": "Bonjour"},
        {"type": "done"},
    ]


def test_should_skip_empty_partials_but_always_close_the_utterance():
    events = run(StubTranscriber(text=""), voice(1), silence(0.8))

    assert events == [{"type": "processing"}, {"type": "final", "text": ""}]


def test_should_report_received_audio_and_peak_level():
    async def send(event):
        pass

    async def feed():
        session = StreamSession(StubTranscriber(), "fr", UtteranceBuffer(), send)
        await session.on_audio(to_pcm(silence(1)))
        await session.on_audio(to_pcm(voice(0.5)))
        return session.audio_stats()

    seconds, peak = asyncio.run(feed())

    assert seconds == 1.5
    assert abs(peak - 0.3) < 0.01
