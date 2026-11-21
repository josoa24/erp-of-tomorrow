from app.common.api_error import ApiException

ACCEPTED_NON_AUDIO_TYPES = {"video/webm", "video/mp4", "application/octet-stream"}


def validate_audio(content: bytes, content_type: str | None, max_bytes: int) -> None:
    if not _is_accepted_type(content_type):
        raise ApiException(415, "VALIDATION", "Format non supporté : envoyez un fichier audio")
    if not content:
        raise ApiException(400, "VALIDATION", "Le fichier audio est vide")
    if len(content) > max_bytes:
        raise ApiException(413, "VALIDATION", f"Le fichier audio dépasse {max_bytes // (1024 * 1024)} Mo")


def _is_accepted_type(content_type: str | None) -> bool:
    if not content_type:
        return False
    base_type = content_type.split(";")[0].strip().lower()
    return base_type.startswith("audio/") or base_type in ACCEPTED_NON_AUDIO_TYPES
