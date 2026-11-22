from __future__ import annotations

from typing import Any

from app.erp.client import ErpClient
from app.tools.spec import NoArgs, ToolSpec, decl

SECTIONS = [
    ("ventes", "/api/vente/sales/kpis"),
    ("stock", "/api/stock/summary"),
    ("approvisionnement", "/api/stock/replenishment"),
    ("equipe", "/api/rh/summary"),
    ("relations", "/api/crm/contacts/summary"),
]


def _section(result: dict[str, Any]) -> Any:
    if not result.get("success"):
        return {"disponible": False}
    return {key: value for key, value in result.items() if key != "success"}


async def get_daily_briefing(erp: ErpClient, a: NoArgs):
    briefing: dict[str, Any] = {}
    for name, path in SECTIONS:
        result = await erp.call("GET", path, params={"onlyNeeded": True} if name == "approvisionnement" else None)
        if result.get("success") and isinstance(result.get("data"), list):
            result = {"success": True, "produits_a_commander": len(result["data"]), "details": result["data"][:5]}
        briefing[name] = _section(result)
    return {"success": True, "briefing": briefing}


SPECS = [
    ToolSpec(decl("get_daily_briefing", "Point du jour : ventes, stock, produits à commander, équipe et relations. Les "
                  "sections non accessibles au rôle sont marquées indisponibles."), NoArgs, get_daily_briefing, "general"),
]
