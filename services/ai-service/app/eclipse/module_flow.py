from __future__ import annotations

from app.eclipse.deps import EclipseDeps
from app.eclipse.generic_module import GENERIC_NOTE, generic_definition
from app.eclipse.schemas import ModuleProposal, ModuleRequest
from app.eclipse.templates import choose_template


async def propose_module(deps: EclipseDeps, body: ModuleRequest, role: str) -> ModuleProposal:
    can_create = role == "ADMIN"
    template = choose_template(body.description)
    if template is not None:
        return ModuleProposal(proposal=template.definition.with_description(body.description), source="TEMPLATE",
                              can_create=can_create, unchecked_columns=list(template.unchecked_columns),
                              hidden_columns=list(template.hidden_columns))
    designed = await deps.designer.design(body.description) if deps.designer is not None else None
    if designed is not None:
        return ModuleProposal(proposal=designed.with_description(body.description), source="GEMINI",
                              can_create=can_create)
    return ModuleProposal(proposal=generic_definition(body.description).with_description(body.description),
                          source="TEMPLATE", can_create=can_create, note=GENERIC_NOTE)
