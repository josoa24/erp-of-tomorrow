"""Configurations Gemini par rôle : outils autorisés + instruction système du jour."""
from __future__ import annotations

from google.genai import types

from app.agent.prompts import build_system_instruction
from app.config import Settings
from app.erp.context import current_user
from app.tools.registry import ToolRegistry

ConfigPair = tuple[types.GenerateContentConfig, types.GenerateContentConfig]


def thinking_config(settings: Settings) -> types.ThinkingConfig | None:
    if settings.gemini_thinking_level:
        return types.ThinkingConfig(thinking_level=settings.gemini_thinking_level.upper())
    if settings.gemini_thinking_budget is not None:
        return types.ThinkingConfig(thinking_budget=settings.gemini_thinking_budget)
    return None


class AgentConfigs:
    def __init__(self, registry: ToolRegistry, settings: Settings) -> None:
        self._registry = registry
        self._settings = settings
        self._cache: dict[str, ConfigPair] = {}

    def for_request(self) -> ConfigPair:
        """(AUTO, NONE) pour le rôle courant, mises en cache par rôle ; instruction système recalculée."""
        user = current_user.get()
        role = user.role if user else None
        key = role or "-"
        if key not in self._cache:
            self._cache[key] = (self._build(types.FunctionCallingConfigMode.AUTO, role),
                                self._build(types.FunctionCallingConfigMode.NONE, role))
        system = build_system_instruction(user=user)
        auto, none = self._cache[key]
        return (auto.model_copy(update={"system_instruction": system}),
                none.model_copy(update={"system_instruction": system}))

    def _build(self, mode: types.FunctionCallingConfigMode, role: str | None) -> types.GenerateContentConfig:
        return types.GenerateContentConfig(
            tools=[self._registry.gemini_tool(role)],
            tool_config=types.ToolConfig(function_calling_config=types.FunctionCallingConfig(mode=mode)),
            # On exécute nous-mêmes les outils : pas de function calling automatique du SDK.
            automatic_function_calling=types.AutomaticFunctionCallingConfig(disable=True),
            thinking_config=thinking_config(self._settings),
            temperature=self._settings.gemini_temperature,
        )
