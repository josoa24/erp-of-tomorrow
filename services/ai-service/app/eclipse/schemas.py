from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.eclipse.definition import ModuleDefinitionRequest
from app.eclipse.definition_parts import CamelModel

MODULE_KEY_PATTERN = r"^[a-z0-9][a-z0-9-]{0,63}$"
Outcome = Literal["ADAPTATION", "ALREADY_ACTIVE", "QUESTION", "UNSUPPORTED", "CREATE_MODULE"]
ProposalSource = Literal["GEMINI", "TEMPLATE"]
ACCESS_TEXT = "Toute l’équipe. Le gérant peut modifier ou supprimer le module."


class AdaptationRequest(CamelModel):
    module_key: str = Field(pattern=MODULE_KEY_PATTERN, description="stock, ventes, relations, equipe, accueil "
                                                                    "ou slug d’un module créé")
    request: str = Field(min_length=1, max_length=500, description="Demande tapée dans la barre Eclipse")


class AdaptationProposal(CamelModel):
    outcome: Outcome
    adaptation_key: str | None = None
    request: str
    title: str | None = None
    summary: str | None = None
    message: str | None = None
    can_apply: bool
    target_module_key: str | None = None


class Suggestion(CamelModel):
    adaptation_key: str
    module_key: str
    title: str
    reason: str


class ModuleRequest(CamelModel):
    description: str = Field(min_length=1, max_length=500, description="Besoin décrit avec les mots de l’utilisateur")


class ModuleProposal(CamelModel):
    proposal: ModuleDefinitionRequest
    access_text: str = ACCESS_TEXT
    source: ProposalSource
    can_create: bool
    unchecked_columns: list[str] = Field(default_factory=list)
    note: str | None = None
