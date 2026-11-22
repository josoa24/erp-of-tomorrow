import logging
import threading
import time

logger = logging.getLogger("voice.preload")


def preload_models(transcriber) -> None:
    started = time.monotonic()
    try:
        sizes = transcriber.preload()
    except Exception:
        logger.exception("chargement du modèle Whisper impossible : il sera retenté à la première transcription")
        return
    logger.info("modèles Whisper prêts (%s) en %.1fs", ", ".join(sizes), time.monotonic() - started)


def start_preload(transcriber) -> threading.Thread:
    thread = threading.Thread(target=preload_models, args=(transcriber,), name="whisper-preload", daemon=True)
    thread.start()
    return thread
