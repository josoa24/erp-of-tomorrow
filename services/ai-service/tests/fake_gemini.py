"""Faux client Gemini pour les tests hors ligne.

Il renvoie des `types.GenerateContentResponse` réels, scénarisés à
l'avance, et enregistre chaque requête pour inspecter l'historique.
"""
from __future__ import annotations

import copy
from typing import Any

from google.genai import types


def fc_response(*calls: tuple[str, dict[str, Any]], signature: bytes = b"sig") -> types.GenerateContentResponse:
    parts = []
    for i, (name, args) in enumerate(calls):
        part = types.Part(function_call=types.FunctionCall(id=f"call_{name}_{i}", name=name, args=args))
        if i == 0:  # comme Gemini 3 : signature sur le 1er function call
            part.thought_signature = signature
        parts.append(part)
    return types.GenerateContentResponse(
        candidates=[types.Candidate(content=types.Content(role="model", parts=parts), finish_reason="STOP")],
        usage_metadata=types.GenerateContentResponseUsageMetadata(prompt_token_count=100, candidates_token_count=10),
    )


def text_response(text: str) -> types.GenerateContentResponse:
    return types.GenerateContentResponse(
        candidates=[types.Candidate(content=types.Content(role="model", parts=[types.Part(text=text)]),
                                    finish_reason="STOP")],
        usage_metadata=types.GenerateContentResponseUsageMetadata(prompt_token_count=120, candidates_token_count=20),
    )


class _Models:
    def __init__(self, script: list[Any]) -> None:
        self.script = list(script)
        self.requests: list[dict[str, Any]] = []

    async def generate_content(self, *, model: str, contents: list, config: types.GenerateContentConfig):
        self.requests.append({"model": model, "contents": copy.deepcopy(contents), "config": config})
        if not self.script:
            raise AssertionError("Plus de réponse scénarisée")
        item = self.script.pop(0)
        if isinstance(item, Exception):
            raise item
        return item


    async def generate_content_stream(self, *, model: str, contents: list, config: types.GenerateContentConfig):
        """Même script que generate_content ; un élément `list` = plusieurs chunks streamés."""
        self.requests.append({"model": model, "contents": copy.deepcopy(contents), "config": config,
                              "stream": True})
        if not self.script:
            raise AssertionError("Plus de réponse scénarisée")
        item = self.script.pop(0)
        if isinstance(item, Exception):
            raise item
        chunks = item if isinstance(item, list) else [item]

        async def gen():
            for c in chunks:
                if isinstance(c, Exception):
                    raise c
                yield c
        return gen()


def text_chunks(*pieces: str) -> list[types.GenerateContentResponse]:
    """Réponse texte découpée en plusieurs chunks (comme le vrai streaming)."""
    return [text_response(p) for p in pieces]


class _Aio:
    def __init__(self, models: _Models) -> None:
        self.models = models

    async def aclose(self) -> None:
        pass


class FakeClient:
    def __init__(self, script: list[Any]) -> None:
        self.models_impl = _Models(script)
        self.aio = _Aio(self.models_impl)

    @property
    def requests(self):
        return self.models_impl.requests
