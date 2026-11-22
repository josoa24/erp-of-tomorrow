from __future__ import annotations

from typing import Any

from app.eclipse.schemas import AdaptationProposal, ModuleProposal
from app.eclipse.text import slugify

ADAPTATION_TOOL = "eclipse_propose_adaptation"
MODULE_TOOL = "eclipse_propose_module"
AuditCalls = tuple[list[dict[str, Any]], str]


def proposed(tool: str, action_id: str) -> list[dict[str, Any]]:
    return [{"name": tool, "status": "pending_confirmation", "action": {"tool": tool, "action_id": action_id}}]


def adaptation_audit(result: AdaptationProposal) -> AuditCalls:
    if result.outcome == "ADAPTATION" and result.adaptation_key:
        return proposed(ADAPTATION_TOOL, result.adaptation_key), "RECOMMENDATION"
    return [{"name": ADAPTATION_TOOL}], "NONE"


def module_audit(result: ModuleProposal) -> AuditCalls:
    return proposed(MODULE_TOOL, slugify(result.proposal.name) or "module"), "RECOMMENDATION"
