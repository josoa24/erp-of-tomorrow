from fastapi import APIRouter, Depends, File, Form, UploadFile
from fastapi.concurrency import run_in_threadpool

from app.common.api_error import ApiException
from app.config import Settings
from app.transcription.audio_validator import validate_audio
from app.transcription.dependencies import get_settings, get_transcriber
from app.transcription.transcriber import AudioDecodingError, Transcriber
from app.transcription.transcription_response import TranscriptionResponse

router = APIRouter()


@router.post("/transcribe", response_model=TranscriptionResponse)
async def transcribe(
    audio: UploadFile = File(...),
    language: str | None = Form(None),
    settings: Settings = Depends(get_settings),
    transcriber: Transcriber = Depends(get_transcriber),
) -> TranscriptionResponse:
    content = await audio.read()
    validate_audio(content, audio.content_type, settings.max_audio_bytes)
    try:
        transcription = await run_in_threadpool(transcriber.transcribe, content, language or settings.language)
    except AudioDecodingError as error:
        raise ApiException(422, "VALIDATION", "Audio illisible : " + str(error)) from error
    return TranscriptionResponse.of(transcription)
