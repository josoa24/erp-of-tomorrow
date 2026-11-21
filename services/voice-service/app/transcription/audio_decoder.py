import io

import av
import numpy as np

from app.streaming.pcm import SAMPLE_RATE
from app.transcription.transcriber import AudioDecodingError


def decode_audio(content: bytes) -> np.ndarray:
    try:
        with av.open(io.BytesIO(content), mode="r") as container:
            resampler = av.AudioResampler(format="s16", layout="mono", rate=SAMPLE_RATE)
            chunks = [_to_array(frame) for decoded in container.decode(audio=0) for frame in resampler.resample(decoded)]
            chunks += [_to_array(frame) for frame in resampler.resample(None)]
    except (av.FFmpegError, IndexError, ValueError) as error:
        raise AudioDecodingError(str(error)) from error
    if not chunks:
        raise AudioDecodingError("aucune piste audio")
    return np.concatenate(chunks).astype(np.float32) / 32768.0


def _to_array(frame) -> np.ndarray:
    return frame.to_ndarray().reshape(-1)
