from __future__ import annotations

from app.eclipse.catalog import adaptations_for
from app.eclipse.deps import EclipseDeps
from app.eclipse.module_registry import active_adaptation_keys
from app.eclipse.modules import accessible_base_modules
from app.eclipse.schemas import Suggestion
from app.erp.context import UserContext


async def list_suggestions(deps: EclipseDeps, user: UserContext) -> list[Suggestion]:
    active = await active_adaptation_keys(deps.erp, user.token)
    if active is None:
        return []
    return [Suggestion(adaptation_key=a.key, module_key=a.module_key, title=a.suggestion, reason=a.reason)
            for a in adaptations_for(accessible_base_modules(user.roles)) if a.key not in active]
