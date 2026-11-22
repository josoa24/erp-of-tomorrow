import pytest

from app.common.api_error import ApiException
from app.transcription.audio_validator import validate_audio

MAX_BYTES = 1024 * 1024


@pytest.mark.parametrize("content_type", ["audio/wav", "audio/webm;codecs=opus", "video/webm", "application/octet-stream"])
def test_should_accept_audio_types(content_type):
    validate_audio(b"data", content_type, MAX_BYTES)


@pytest.mark.parametrize("content_type", [None, "", "text/plain", "image/png"])
def test_should_reject_non_audio_types_with_415(content_type):
    with pytest.raises(ApiException) as error:
        validate_audio(b"data", content_type, MAX_BYTES)

    assert error.value.status == 415


def test_should_reject_empty_audio_with_400():
    with pytest.raises(ApiException) as error:
        validate_audio(b"", "audio/wav", MAX_BYTES)

    assert error.value.status == 400


def test_should_reject_too_large_audio_with_413():
    with pytest.raises(ApiException) as error:
        validate_audio(b"x" * (MAX_BYTES + 1), "audio/wav", MAX_BYTES)

    assert error.value.status == 413
    assert "1 Mo" in error.value.message
