from __future__ import annotations

from app.eclipse.deps import EclipseDeps
from app.eclipse.generic_module import GENERIC_NOTE, generic_definition
from app.eclipse.module_designer import DesignedModule
from app.eclipse.schemas import ModuleProposal, ModuleRefineRequest, ModuleRequest
from app.eclipse.templates import choose_template

UNAVAILABLE_NOTE = "Eclipse n’a pas pu ajuster ce module pour le moment : la proposition reste inchangée."


def _from_design(designed: DesignedModule, description: str, can_create: bool, keep_optional: bool = True) -> ModuleProposal:
    known = {column.key for column in designed.definition.columns} if keep_optional else set()
    return ModuleProposal(
        proposal=designed.definition.with_description(description), source="GEMINI", can_create=can_create,
        unchecked_columns=[key for key in designed.optional_columns if key in known],
        explanation=designed.explanation or None, suggestions=designed.suggestions, changes=designed.changes)


async def propose_module(deps: EclipseDeps, body: ModuleRequest, role: str) -> ModuleProposal:
    can_create = role == "ADMIN"
    designed = await deps.designer.design(body.description) if deps.designer is not None else None
    if designed is not None:
        return _from_design(designed, body.description, can_create)
    template = choose_template(body.description)
    if template is not None:
        return ModuleProposal(proposal=template.definition.with_description(body.description), source="TEMPLATE",
                              can_create=can_create, unchecked_columns=list(template.unchecked_columns))
    return ModuleProposal(proposal=generic_definition(body.description).with_description(body.description),
                          source="TEMPLATE", can_create=can_create, note=GENERIC_NOTE)


async def refine_module(deps: EclipseDeps, body: ModuleRefineRequest, role: str) -> ModuleProposal:
    can_create = role == "ADMIN"
    designed = await deps.designer.refine(body.proposal, body.instruction) if deps.designer is not None else None
    if designed is None:
        return ModuleProposal(proposal=body.proposal, source="TEMPLATE", can_create=can_create, note=UNAVAILABLE_NOTE)
    return _from_design(designed, body.proposal.description or "", can_create, keep_optional=False)
