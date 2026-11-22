from fastapi import Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.common.api_error import to_body


def describe(error: RequestValidationError) -> str:
    fields = sorted({str(item["loc"][-1]) for item in error.errors() if item.get("loc")})
    return "Requête invalide : champ manquant ou invalide (" + ", ".join(fields) + ")" if fields else "Requête invalide"


async def handle_validation_error(_: Request, error: RequestValidationError) -> JSONResponse:
    return JSONResponse(status_code=400, content=to_body(400, "VALIDATION", describe(error)))
