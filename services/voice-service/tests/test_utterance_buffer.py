from app.streaming.pcm import SAMPLE_RATE
from app.streaming.utterance_buffer import UtteranceBuffer
from tests.audio_samples import silence, voice


def test_should_ignore_silence_and_keep_only_leading_audio():
    buffer = UtteranceBuffer()

    buffer.append(silence(3))

    assert not buffer.has_speech
    assert not buffer.needs_partial()
    assert not buffer.is_complete()
    assert buffer.samples().size == int(0.3 * SAMPLE_RATE)


def test_should_ask_partial_after_interval_of_speech():
    buffer = UtteranceBuffer(partial_interval_seconds=0.8)

    buffer.append(voice(0.5))
    assert not buffer.needs_partial()

    buffer.append(voice(0.5))
    assert buffer.needs_partial()

    buffer.mark_partial()
    assert not buffer.needs_partial()


def test_should_complete_after_silence_following_speech():
    buffer = UtteranceBuffer(silence_seconds=0.7)
    buffer.append(voice(1))

    buffer.append(silence(0.4))
    assert not buffer.is_complete()

    buffer.append(silence(0.4))
    assert buffer.is_complete()


def test_should_complete_when_utterance_is_too_long():
    buffer = UtteranceBuffer(max_seconds=2)

    buffer.append(voice(2.1))

    assert buffer.is_complete()


def test_should_start_over_after_reset():
    buffer = UtteranceBuffer()
    buffer.append(voice(1))

    buffer.reset()

    assert not buffer.has_speech
    assert buffer.samples().size == 0
