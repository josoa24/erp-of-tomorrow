from __future__ import annotations

from typing import Annotated, Literal

from pydantic import BaseModel, ConfigDict, Field, StringConstraints, model_validator
from pydantic.alias_generators import to_camel

ColumnType = Literal["TEXT", "NUMBER", "MONEY", "DATE", "TIME", "DATETIME", "CHOICE", "REFERENCE"]
ReferenceTarget = Literal["CUSTOMER", "SALE", "EMPLOYEE", "PRODUCT", "SUPPLIER"]
Tone = Literal["SUCCESS", "WARNING", "CRITICAL", "INFO", "NEUTRAL"]
IndicatorKind = Literal["COUNT", "SUM", "RATIO_TODAY"]
ModuleIcon = Literal["truck", "wrench", "calendar-days", "package", "clipboard-list", "layout-grid", "users",
                     "receipt"]
IndicatorIcon = Literal["truck", "wrench", "calendar-days", "package", "clipboard-list", "layout-grid", "users",
                        "receipt", "clock", "circle-check", "wallet"]
KEY_PATTERN = r"^[a-z][a-z0-9_]{0,39}$"
Choice = Annotated[str, StringConstraints(strip_whitespace=True, min_length=1, max_length=60)]


class CamelModel(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True, str_strip_whitespace=True)


class ColumnSpec(CamelModel):
    key: str = Field(pattern=KEY_PATTERN)
    label: str = Field(min_length=1, max_length=60)
    type: ColumnType
    required: bool = False
    choices: list[Choice] | None = Field(default=None, max_length=20)
    reference: ReferenceTarget | None = None
    listed: bool = True

    @model_validator(mode="after")
    def _typed_options(self) -> ColumnSpec:
        if (self.type == "CHOICE") != bool(self.choices):
            raise ValueError(f"colonne {self.key} : choices est réservé et obligatoire pour CHOICE")
        if self.choices and len(set(self.choices)) != len(self.choices):
            raise ValueError(f"colonne {self.key} : un choix apparaît deux fois")
        if (self.type == "REFERENCE") != (self.reference is not None):
            raise ValueError(f"colonne {self.key} : reference est réservé et obligatoire pour REFERENCE")
        return self


class StatusSpec(CamelModel):
    key: str = Field(pattern=KEY_PATTERN)
    label: str = Field(min_length=1, max_length=40)
    tone: Tone
    final: bool = False


class IndicatorSpec(CamelModel):
    key: str = Field(pattern=KEY_PATTERN)
    label: str = Field(min_length=1, max_length=40)
    icon: IndicatorIcon
    kind: IndicatorKind
    statuses: list[Annotated[str, StringConstraints(min_length=1)]] | None = Field(default=None, max_length=6)
    column: str | None = None
    today_column: str | None = None
    detail_column: str | None = None
