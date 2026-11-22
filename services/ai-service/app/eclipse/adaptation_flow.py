from __future__ import annotations

from typing import Sequence

from app.eclipse.catalog import ADAPTATIONS_BY_KEY, Adaptation, adaptation_scope
from app.eclipse.deps import EclipseDeps
from app.eclipse.matcher import Match, match
from app.eclipse.messages import already_active, unsupported_from_home, unsupported_in_module
from app.eclipse.module_registry import active_adaptation_keys, custom_module_name
from app.eclipse.modules import BASE_MODULE_LABELS, HOME_MODULE, is_base_module, slug_label
from app.eclipse.schemas import AdaptationProposal, AdaptationRequest
from app.erp.context import UserContext


async def screen_label(deps: EclipseDeps, module_key: str, token: str) -> str:
    if module_key == HOME_MODULE:
        return "Accueil"
    if is_base_module(module_key):
        return BASE_MODULE_LABELS[module_key]
    return await custom_module_name(deps.erp, token, module_key) or slug_label(module_key)


async def recognize(deps: EclipseDeps, request: str, screen: str, scope: Sequence[Adaptation]) -> Match:
    found = match(request, scope)
    if found.keyword_hit or deps.classifier is None:
        return found
    verdict = await deps.classifier.classify(request, screen, scope)
    if verdict is None or (found.adaptation_key and verdict.intent == "CREATE_MODULE"):
        return found
    return verdict


def unsupported_message(module_key: str, screen: str, scope: Sequence[Adaptation], removal: bool) -> str:
    if module_key == HOME_MODULE:
        return unsupported_from_home(scope, removal)
    return unsupported_in_module(screen, scope, removal)


async def adaptation_outcome(deps: EclipseDeps, adaptation: Adaptation, body: AdaptationRequest,
                             user: UserContext) -> AdaptationProposal:
    active = await active_adaptation_keys(deps.erp, user.token) or set()
    is_active = adaptation.key in active
    return AdaptationProposal(
        outcome="ALREADY_ACTIVE" if is_active else "ADAPTATION", adaptation_key=adaptation.key,
        request=body.request, title=adaptation.title, summary=adaptation.summary,
        message=already_active(adaptation) if is_active else None, can_apply=user.role == "ADMIN",
        target_module_key=adaptation.module_key if body.module_key == HOME_MODULE else None)


async def propose_adaptation(deps: EclipseDeps, body: AdaptationRequest, user: UserContext) -> AdaptationProposal:
    scope = adaptation_scope(body.module_key, user.role)
    screen = await screen_label(deps, body.module_key, user.token)
    found = await recognize(deps, body.request, screen, scope)
    if found.intent == "ADAPTATION" and found.adaptation_key:
        return await adaptation_outcome(deps, ADAPTATIONS_BY_KEY[found.adaptation_key], body, user)
    unsupported = found.intent == "UNSUPPORTED"
    message = unsupported_message(body.module_key, screen, scope, found.removal) if unsupported else None
    return AdaptationProposal(outcome=found.intent, request=body.request, message=message,
                              can_apply=user.role == "ADMIN")
