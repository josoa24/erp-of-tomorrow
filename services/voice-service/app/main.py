from fastapi import FastAPI

from app.common.api_error import ApiException, handle_api_exception
from app.transcription.transcription_controller import router as transcription_router


def create_app() -> FastAPI:
    app = FastAPI(title="voice-service")
    app.add_exception_handler(ApiException, handle_api_exception)
    app.include_router(transcription_router)

    @app.get("/health")
    def health() -> dict:
        return {"status": "UP"}

    return app


app = create_app()
