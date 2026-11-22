from __future__ import annotations

from fastapi import APIRouter, Depends, Request

from app.api.identity import user_from_request
from app.api.rate_limit import enforce_rate_limit
from app.api.schemas import api_error
from app.api.turn_support import record_interaction
from app.eclipse.adaptation_flow import propose_adaptation
from app.eclipse.audit_calls import adaptation_audit, module_audit
from app.eclipse.messages import forbidden_module
from app.eclipse.module_flow import propose_module
from app.eclipse.modules import can_access
from app.eclipse.schemas import AdaptationProposal, AdaptationRequest, ModuleProposal, ModuleRequest, Suggestion
from app.eclipse.suggestions import list_suggestions

router = APIRouter(prefix="/eclipse", tags=["eclipse"])
LIMITED = [Depends(enforce_rate_limit)]


@router.post("/adaptations/propose", response_model=AdaptationProposal, response_model_exclude_none=True,
             dependencies=LIMITED, summary="Reconnaît une demande d’adaptation d’écran (catalogue fermé)",
             description="Issue ADAPTATION, ALREADY_ACTIVE, QUESTION, UNSUPPORTED ou CREATE_MODULE. "
                         "Rien n’est appliqué : le front applique via /api/modules/adaptations (ADMIN).")
async def propose_screen_adaptation(body: AdaptationRequest, request: Request):
    user = user_from_request(request)
    if not can_access(body.module_key, user.role):
        return api_error(403, "FORBIDDEN", forbidden_module(user.role, body.module_key))
    result = await propose_adaptation(request.app.state.eclipse, body, user)
    calls, label = adaptation_audit(result)
    record_interaction(request, user, None, body.request, calls, label)
    return result


@router.post("/modules/propose", response_model=ModuleProposal, response_model_exclude_none=True,
             dependencies=LIMITED, summary="Prépare la définition d’un module à partir d’un besoin",
             description="Modèle (Livraisons, Réparations, Rendez-vous), Gemini si la clé est configurée, "
                         "sinon base générique avec une note. Rien n’est créé.")
async def propose_custom_module(body: ModuleRequest, request: Request):
    user = user_from_request(request)
    result = await propose_module(request.app.state.eclipse, body, user.role)
    calls, label = module_audit(result)
    record_interaction(request, user, None, body.description, calls, label)
    return result


@router.get("/suggestions", response_model=list[Suggestion],
            summary="Adaptations du catalogue pas encore actives, pour les modules du rôle")
async def adaptation_suggestions(request: Request):
    user = user_from_request(request)
    return await list_suggestions(request.app.state.eclipse, user)
