from datetime import datetime, timezone

from fastapi import Request
from fastapi.responses import JSONResponse


class ApiException(Exception):
    def __init__(self, status: int, type_: str, message: str):
        super().__init__(message)
        self.status = status
        self.type = type_
        self.message = message


def to_body(status: int, type_: str, message: str) -> dict:
    return {
        "status": status,
        "type": type_,
        "message": message,
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }


async def handle_api_exception(_: Request, error: ApiException) -> JSONResponse:
    return JSONResponse(status_code=error.status, content=to_body(error.status, error.type, error.message))
