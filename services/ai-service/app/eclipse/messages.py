from __future__ import annotations

from typing import Sequence

from app.eclipse.catalog import Adaptation
from app.eclipse.modules import BASE_MODULE_LABELS


def offers_in_module(adaptations: Sequence[Adaptation]) -> str:
    return " ; ".join(adaptation.offer for adaptation in adaptations)


def offers_across_modules(adaptations: Sequence[Adaptation]) -> str:
    return " ; ".join(f"{a.offer} dans {BASE_MODULE_LABELS[a.module_key]}" for a in adaptations)


def unsupported_in_module(module_label: str, scope: Sequence[Adaptation]) -> str:
    opening = f"Je ne sais pas encore ajouter cela à « {module_label} »."
    if not scope:
        return f"{opening} Vous pouvez me poser une question sur ses données."
    return f"{opening} Je peux ajouter : {offers_in_module(scope)}."


def unsupported_from_home(scope: Sequence[Adaptation]) -> str:
    opening = "Je ne sais pas encore faire cela depuis l’accueil."
    if not scope:
        return f"{opening} Vous pouvez me poser une question sur vos données."
    return f"{opening} Je peux ajouter : {offers_across_modules(scope)}."


def already_active(adaptation: Adaptation) -> str:
    module_label = BASE_MODULE_LABELS[adaptation.module_key]
    return f"« {adaptation.title} » est déjà appliqué pour l’équipe dans « {module_label} »."


def forbidden_module(role: str, module_key: str) -> str:
    return f"Le rôle {role} n’a pas accès au module « {BASE_MODULE_LABELS[module_key]} »."
