from __future__ import annotations

from pydantic import Field, model_validator

from app.eclipse.definition_parts import CamelModel, ColumnSpec, IndicatorSpec, ModuleIcon, StatusSpec
from app.eclipse.definition_rules import check_definition


class ModuleDefinitionRequest(CamelModel):
    name: str = Field(min_length=2, max_length=40)
    icon: ModuleIcon
    description: str | None = Field(default=None, max_length=280)
    columns: list[ColumnSpec] = Field(min_length=1, max_length=12)
    statuses: list[StatusSpec] = Field(min_length=2, max_length=6)
    indicators: list[IndicatorSpec] = Field(default_factory=list, max_length=4)

    @model_validator(mode="after")
    def _coherent(self) -> ModuleDefinitionRequest:
        check_definition(self.columns, self.statuses, self.indicators)
        return self

    def with_description(self, description: str) -> ModuleDefinitionRequest:
        return self.model_copy(update={"description": description.strip()[:280] or None})
