"""POST /invoices/extract : lecture d'une facture fournisseur + contrôle Invoice Shield (rien n'est enregistré)."""
from __future__ import annotations

from fastapi import APIRouter, Depends, Request

from app.api.identity import user_from_request
from app.api.rate_limit import enforce_rate_limit
from app.api.schemas import api_error
from app.api.turn_support import record_interaction
from app.erp.client import ErpClient
from app.erp.context import current_user
from app.invoices.shield_check import analyze
from app.invoices.upload import UploadError, extract_file, read_body
from app.tools.catalog import module_allowed

router = APIRouter(prefix="/invoices", tags=["invoices"], dependencies=[Depends(enforce_rate_limit)])
_DOC = {"content": {"multipart/form-data": {"schema": {"type": "object", "required": ["file"], "properties": {
    "file": {"type": "string", "format": "binary", "description": "Image JPEG, PNG, WEBP ou PDF"}}}}}}


@router.post("/extract", openapi_extra={"requestBody": _DOC})
async def extract_invoice(request: Request):
    """Extrait les champs (Gemini, sans outil), retrouve le fournisseur et renvoie le rapport du shield :
    `{extracted, supplierMatch, shield}` (+ `shieldError` si le contrôle n'a pas pu être fait)."""
    user = user_from_request(request)
    if not module_allowed("stock", user.role):
        return api_error(403, "FORBIDDEN", f"Le rôle {user.role} n'a pas accès aux factures fournisseurs.")
    extractor = request.app.state.invoice_extractor
    if extractor is None:
        return api_error(503, "SERVER", "GEMINI_API_KEY manquante : assistant indisponible.")
    max_bytes = request.app.state.settings.invoice_max_bytes
    try:
        data, mime = extract_file(request.headers.get("content-type", ""), await read_body(request, max_bytes),
                                  max_bytes)
    except UploadError as exc:
        return api_error(exc.status, exc.type, exc.message)
    extracted = await extractor.extract(data, mime)
    token = current_user.set(user)
    try:
        result = await analyze(ErpClient(request.app.state.erp_http), extracted)
    finally:
        current_user.reset(token)
    checked = result["shield"] is not None or "status" in result.get("shieldError", {})  # appel ERP fait
    tools = ["extract_invoice", "search_suppliers"] + (["check_supplier_invoice"] if checked else [])
    record_interaction(request, user, None, "[Extraction de facture fournisseur]",
                       [{"name": t} for t in tools], "ESTIMATION")
    return result
