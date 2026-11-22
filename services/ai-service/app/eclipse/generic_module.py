from __future__ import annotations

import re

from app.eclipse.definition import ModuleDefinitionRequest
from app.eclipse.module_slug import BUILT_IN_MODULES, slug_from_name
from app.eclipse.template_parts import column, definition, indicator, status
from app.eclipse.text import normalize

GENERIC_NOTE = ("Eclipse n’a pas de modèle pour ce besoin : voici une base simple (nom, date, montant, statut) "
                "à ajuster avant la création.")
DEFAULT_NAME = "Nouveau module"
MAX_NAME_WORDS = 3
_WORD = re.compile(r"[^\W\d_]+(?:-[^\W\d_]+)*")
_FILLERS = frozenset((
    "je j nous on il veux voudrais aimerais souhaite souhaiterais voulons faut besoin merci svp stp "
    "cree creer creez creons creation fais faire ajoute ajouter construis construire nouveau nouvelle nouvel "
    "module modules outil tableau liste suivre suivi gerer gestion noter enregistrer pouvoir permettre "
    "un une des de du d le la les l mes mon ma nos notre vos votre ses son sa leurs leur "
    "pour par avec sans sur dans en au aux a et ou qui que quoi me m moi te t se s y ce cet cette ces "
    "tous toutes tout toute chaque ici").split())


def module_name(description: str) -> str:
    picked: list[str] = []
    for word in _WORD.findall(description):
        if normalize(word) in _FILLERS:
            if picked:
                break
            continue
        picked.append(word.lower())
        if len(picked) == MAX_NAME_WORDS:
            break
    name = " ".join(picked)[:40].strip()
    if len(name) < 2 or slug_from_name(name) in BUILT_IN_MODULES:
        return DEFAULT_NAME
    return name[:1].upper() + name[1:]


def generic_definition(description: str) -> ModuleDefinitionRequest:
    return definition(module_name(description), "layout-grid", [
        column("nom", "Nom", "TEXT", required=True),
        column("date", "Date", "DATE"),
        column("montant", "Montant", "MONEY"),
    ], [
        status("a_faire", "À faire", "NEUTRAL"),
        status("en_cours", "En cours", "INFO"),
        status("termine", "Terminé", "SUCCESS", final=True),
    ], [
        indicator("a_traiter", "À traiter", "clipboard-list", "COUNT", statuses=["a_faire", "en_cours"]),
    ])
