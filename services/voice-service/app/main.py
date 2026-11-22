import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from fastapi.staticfiles import StaticFiles

from app.common.api_error import ApiException, handle_api_exception
from app.common.validation_error import handle_validation_error
from app.demo.demo_controller import STATIC_DIR
from app.demo.demo_controller import router as demo_router
from app.streaming.stream_controller import router as stream_router
from app.transcription.dependencies import get_transcriber
from app.transcription.model_preloader import start_preload
from app.transcription.transcription_controller import router as transcription_router


def create_app(preload: bool = False) -> FastAPI:
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s %(message)s")

    @asynccontextmanager
    async def lifespan(_: FastAPI):
        if preload:
            start_preload(get_transcriber())
        yield

    app = FastAPI(title="voice-service", lifespan=lifespan)
    app.add_exception_handler(ApiException, handle_api_exception)
    app.add_exception_handler(RequestValidationError, handle_validation_error)
    app.include_router(transcription_router)
    app.include_router(stream_router)
    app.include_router(demo_router)
    app.mount("/demo/static", StaticFiles(directory=STATIC_DIR), name="demo-static")

    @app.get("/health")
    def health() -> dict:
        return {"status": "UP"}

    return app


app = create_app(preload=True)
