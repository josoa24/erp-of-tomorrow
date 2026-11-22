import json
import logging
import uuid

from fastapi import APIRouter, Depends, WebSocket, WebSocketDisconnect

from app.config import Settings
from app.streaming.stream_session import StreamSession
from app.streaming.utterance_buffer import UtteranceBuffer
from app.streaming.utterance_logger import UtteranceLogger
from app.transcription.dependencies import get_settings, get_transcriber, get_utterance_logger
from app.transcription.transcriber import SampleTranscriber

router = APIRouter()
logger = logging.getLogger("voice.stream")

STOP_MESSAGE = "stop"


@router.websocket("/stream")
async def stream(
    websocket: WebSocket,
    language: str | None = None,
    settings: Settings = Depends(get_settings),
    transcriber: SampleTranscriber = Depends(get_transcriber),
    utterance_logger: UtteranceLogger = Depends(get_utterance_logger),
) -> None:
    await websocket.accept()
    session_id = uuid.uuid4().hex[:6]
    chosen_language = language or settings.language
    logger.info("[%s] connexion ouverte langue=%s", session_id, chosen_language)
    session = StreamSession(
        transcriber, chosen_language, UtteranceBuffer(), websocket.send_json, utterance_logger, session_id
    )
    try:
        while True:
            message = await websocket.receive()
            if message["type"] == "websocket.disconnect":
                return
            if message.get("bytes"):
                await session.on_audio(message["bytes"])
            elif message.get("text") == STOP_MESSAGE:
                logger.info("[%s] arret demande", session_id)
                await session.finish()
            elif message.get("text"):
                _log_client_message(session_id, message["text"])
    except WebSocketDisconnect:
        return
    finally:
        await session.close()
        seconds, peak = session.audio_stats()
        logger.info("[%s] connexion fermee audio_recu=%.1fs niveau_max=%.3f", session_id, seconds, peak)


def _log_client_message(session_id: str, text: str) -> None:
    try:
        payload = json.loads(text)
    except ValueError:
        return
    if isinstance(payload, dict) and payload.get("type") == "log":
        logger.info("[%s] navigateur : %s", session_id, str(payload.get("message", ""))[:500])
