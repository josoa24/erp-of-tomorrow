from __future__ import annotations

from dataclasses import dataclass

from app.eclipse.modules import HOME_MODULE, accessible_base_modules


@dataclass(frozen=True)
class Adaptation:
    key: str
    module_key: str
    title: str
    summary: str
    offer: str
    suggestion: str
    reason: str


CATALOG: tuple[Adaptation, ...] = (
    Adaptation(
        "stock.lots-peremption", "stock", "N° de lot et péremption",
        "Eclipse a ajouté : colonnes N° de lot et Péremption · alerte J-30 · filtre Expire bientôt",
        "le n° de lot et la date de péremption",
        "Eclipse suggère un champ « N° de lot » dans Stock",
        "Vous suivez des produits périssables : Eclipse peut ajouter le n° de lot et la péremption."),
    Adaptation(
        "ventes.mobile-money-credit", "ventes", "Paiement Mobile Money et ventes à crédit",
        "Eclipse a ajouté : colonne Paiement · filtre À crédit · indicateur Crédit en cours",
        "le paiement Mobile Money et le suivi du crédit",
        "Eclipse suggère le suivi des paiements dans Ventes",
        "Vos clients paient aussi par Mobile Money ou à crédit : Eclipse peut ajouter le moyen de paiement "
        "et le suivi du crédit."),
    Adaptation(
        "relations.nif-plafond-credit", "relations", "NIF / STAT et plafond de crédit",
        "Eclipse a ajouté : colonnes NIF / STAT et Plafond crédit · filtre Professionnels · alerte de dépassement",
        "le NIF / STAT et un plafond de crédit",
        "Eclipse suggère les champs « NIF / STAT » dans Relations",
        "Vous vendez à des professionnels : Eclipse peut ajouter le NIF, le STAT et un plafond de crédit."),
    Adaptation(
        "equipe.avances-salaire", "equipe", "Avances sur salaire",
        "Eclipse a ajouté : colonnes Avance et Plafond · filtre Avance demandée · validation par le gérant",
        "les avances sur salaire",
        "Eclipse suggère le suivi des avances dans Équipe",
        "Vos employés demandent des avances sur salaire : Eclipse peut les suivre avec un plafond de 30 % "
        "du salaire."),
)
ADAPTATIONS_BY_KEY: dict[str, Adaptation] = {adaptation.key: adaptation for adaptation in CATALOG}


def adaptations_for(module_keys: list[str]) -> list[Adaptation]:
    return [adaptation for adaptation in CATALOG if adaptation.module_key in module_keys]


def adaptation_scope(module_key: str, role: str | tuple[str, ...]) -> list[Adaptation]:
    if module_key == HOME_MODULE:
        return adaptations_for(accessible_base_modules(role))
    return adaptations_for([module_key])
