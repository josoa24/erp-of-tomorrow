"""Lecture sûre du document envoyé (multipart/form-data, champ `file`) : taille, type réel, contenu.

Le multipart est analysé avec la bibliothèque standard (module email) : aucune dépendance ajoutée.
Le type est déterminé par les premiers octets du fichier, pas seulement par ce que déclare le client.
"""
from __future__ import annotations

from email import policy
from email.parser import BytesParser

from fastapi import Request

ALLOWED_TYPES = {"image/jpeg", "image/png", "image/webp", "application/pdf"}
_MULTIPART_OVERHEAD = 64 * 1024  # en-têtes et délimiteurs autour du fichier


class UploadError(Exception):
    def __init__(self, status: int, type_: str, message: str) -> None:
        super().__init__(message)
        self.status, self.type, self.message = status, type_, message


def sniff_type(data: bytes) -> str | None:
    if data.startswith(b"%PDF-"):
        return "application/pdf"
    if data.startswith(b"\x89PNG\r\n\x1a\n"):
        return "image/png"
    if data.startswith(b"\xff\xd8\xff"):
        return "image/jpeg"
    if data[:4] == b"RIFF" and data[8:12] == b"WEBP":
        return "image/webp"
    return None


def _too_large(max_bytes: int) -> UploadError:
    return UploadError(413, "VALIDATION", f"Fichier trop volumineux (maximum {max_bytes // (1024 * 1024)} Mo).")


async def read_body(request: Request, max_bytes: int) -> bytes:
    """Lit le corps en s'arrêtant dès que la limite est dépassée (pas de chargement illimité)."""
    limit = max_bytes + _MULTIPART_OVERHEAD
    if int(request.headers.get("content-length") or 0) > limit:
        raise _too_large(max_bytes)
    chunks, size = [], 0
    async for chunk in request.stream():
        size += len(chunk)
        if size > limit:
            raise _too_large(max_bytes)
        chunks.append(chunk)
    return b"".join(chunks)


def extract_file(content_type: str, body: bytes, max_bytes: int, field: str = "file") -> tuple[bytes, str]:
    """Renvoie (octets, type réel) du champ `file`, ou lève UploadError (400 / 413)."""
    if not content_type.lower().startswith("multipart/form-data"):
        raise UploadError(400, "VALIDATION", "Envoyer le document en multipart/form-data (champ « file »).")
    message = BytesParser(policy=policy.HTTP).parsebytes(
        b"Content-Type: " + content_type.encode("latin-1") + b"\r\n\r\n" + body)
    part = next((p for p in message.iter_parts()
                 if p.get_param("name", header="content-disposition") == field), None)
    data = part.get_payload(decode=True) if part is not None else None
    if not data:
        raise UploadError(400, "VALIDATION", "Champ « file » absent ou vide.")
    if len(data) > max_bytes:
        raise _too_large(max_bytes)
    real = sniff_type(data)
    if real not in ALLOWED_TYPES or part.get_content_type() not in ALLOWED_TYPES | {"application/octet-stream"}:
        raise UploadError(400, "VALIDATION", "Type de fichier refusé : image JPEG, PNG, WEBP ou PDF uniquement.")
    return data, real
