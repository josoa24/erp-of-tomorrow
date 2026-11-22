from __future__ import annotations

import logging

from app.erp.client import ErpClient

ADAPTATIONS_PATH = "/api/modules/adaptations"
DEFINITIONS_PATH = "/api/modules/definitions"
logger = logging.getLogger(__name__)


async def active_adaptation_keys(erp: ErpClient, token: str) -> set[str] | None:
    result = await erp.call("GET", ADAPTATIONS_PATH, token=token)
    if not result.get("success"):
        logger.warning("Adaptations actives illisibles : %s", result.get("error", {}).get("code"))
        return None
    items = result.get("data") or []
    return {str(item.get("adaptationKey")) for item in items if isinstance(item, dict)}


async def custom_module_name(erp: ErpClient, token: str, slug: str) -> str | None:
    result = await erp.call("GET", f"{DEFINITIONS_PATH}/{slug}", token=token)
    name = result.get("name") if result.get("success") else None
    return name if isinstance(name, str) and name.strip() else None
