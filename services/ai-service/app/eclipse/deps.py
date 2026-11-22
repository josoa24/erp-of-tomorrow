from __future__ import annotations

from dataclasses import dataclass
from typing import Any

import httpx

from app.config import Settings
from app.eclipse.gemini_designer import ModuleDesigner
from app.eclipse.gemini_intent import IntentClassifier
from app.erp.client import ErpClient


@dataclass(frozen=True)
class EclipseDeps:
    erp: ErpClient
    classifier: IntentClassifier | None = None
    designer: ModuleDesigner | None = None


def build_eclipse(gemini: Any, erp_http: httpx.AsyncClient, settings: Settings) -> EclipseDeps:
    erp = ErpClient(erp_http)
    if gemini is None:
        return EclipseDeps(erp)
    return EclipseDeps(erp, IntentClassifier(gemini, settings), ModuleDesigner(gemini, settings))
