from pathlib import Path

from fastapi import APIRouter
from fastapi.responses import FileResponse

STATIC_DIR = Path(__file__).parent / "static"

router = APIRouter()


@router.get("/demo", include_in_schema=False)
def demo() -> FileResponse:
    return FileResponse(STATIC_DIR / "index.html")
