from __future__ import annotations

import logging
from typing import Any

from google.genai import types
from pydantic import BaseModel, Field, ValidationError

from app.agent.gemini_config import thinking_config
from app.config import Settings
from app.eclipse.definition import ModuleDefinitionRequest
from app.eclipse.definition_parts import CamelModel
from app.eclipse.designer_prompt import INSTRUCTION, REFINE_INSTRUCTION, examples
from app.eclipse.schema_simplify import simplify

logger = logging.getLogger(__name__)
MAX_ATTEMPTS = 3


class DesignedModule(CamelModel):
    definition: ModuleDefinitionRequest
    explanation: str = Field(default="", max_length=400)
    suggestions: list[str] = Field(default_factory=list, max_length=3)
    changes: list[str] = Field(default_factory=list, max_length=12)
    optional_columns: list[str] = Field(default_factory=list, max_length=12)


class ModuleDesigner:
    def __init__(self, client: Any, settings: Settings) -> None:
        self._client = client
        self._settings = settings
        self._schema = simplify(DesignedModule.model_json_schema(by_alias=True))

    def _config(self, instruction: str) -> types.GenerateContentConfig:
        return types.GenerateContentConfig(
            system_instruction=instruction, response_mime_type="application/json",
            response_json_schema=self._schema, temperature=0.4, thinking_config=thinking_config(self._settings))

    async def _generate(self, instruction: str, contents: list[types.Content]) -> DesignedModule | None:
        history = list(contents)
        for _ in range(MAX_ATTEMPTS):
            try:
                response = await self._client.aio.models.generate_content(
                    model=self._settings.gemini_model, config=self._config(instruction), contents=history)
                return DesignedModule.model_validate_json(response.text or "")
            except ValidationError as exc:
                problem = "; ".join(error["msg"] for error in exc.errors())[:600]
                logger.warning("Définition de module invalide, nouvelle tentative : %s", problem)
                history = [*contents, types.Content(role="user", parts=[types.Part.from_text(
                    text=f"Ta réponse a été refusée par le validateur : {problem}. Corrige et renvoie le JSON complet.")])]
            except Exception as exc:
                logger.warning("Conception de module Gemini indisponible : %s", type(exc).__name__)
                return None
        return None

    async def design(self, description: str) -> DesignedModule | None:
        prompt = f"{examples()}\n\nBesoin de l'utilisateur : {description}"
        return await self._generate(INSTRUCTION, [types.Content(role="user", parts=[types.Part.from_text(text=prompt)])])

    async def refine(self, current: ModuleDefinitionRequest, request: str) -> DesignedModule | None:
        state = current.model_dump_json(by_alias=True, exclude_none=True)
        prompt = f"Définition actuelle :\n{state}\n\nDemande d'ajustement : {request}"
        return await self._generate(REFINE_INSTRUCTION, [types.Content(role="user", parts=[types.Part.from_text(text=prompt)])])
