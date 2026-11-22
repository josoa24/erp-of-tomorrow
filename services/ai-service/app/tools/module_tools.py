from __future__ import annotations

from typing import Any

from pydantic import Field

from app.erp.client import ErpClient, keep
from app.tools.spec import WRITE, Args, NoArgs, S, T, ToolSpec, decl, integer, string


class SlugArgs(Args):
    slug: str = Field(min_length=1, max_length=64, pattern=r"^[a-z0-9][a-z0-9-]*$")


class RecordsArgs(SlugArgs):
    search: str | None = Field(default=None, max_length=100)
    status: str | None = Field(default=None, max_length=64)


class NewRecordArgs(SlugArgs):
    values: dict[str, Any]


class AdvanceArgs(SlugArgs):
    record_id: int = Field(ge=1)


async def list_modules(erp: ErpClient, a: NoArgs):
    return await erp.call("GET", "/api/modules/definitions")


async def get_module_definition(erp: ErpClient, a: SlugArgs):
    return await erp.call("GET", f"/api/modules/definitions/{a.slug}")


async def get_module_records(erp: ErpClient, a: RecordsArgs):
    return keep(await erp.call("GET", f"/api/modules/definitions/{a.slug}/records",
                               params={"search": a.search, "status": a.status, "size": 30}), 30)


async def get_module_indicators(erp: ErpClient, a: SlugArgs):
    return await erp.call("GET", f"/api/modules/definitions/{a.slug}/indicators")


async def add_module_record(erp: ErpClient, a: NewRecordArgs):
    return await erp.call("POST", f"/api/modules/definitions/{a.slug}/records", json={"values": a.values})


async def advance_module_record(erp: ErpClient, a: AdvanceArgs):
    return await erp.call("POST", f"/api/modules/definitions/{a.slug}/records/{a.record_id}/advance")


_SLUG = {"slug": string("Identifiant du module (ex. livraisons).")}

SPECS = [
    ToolSpec(decl("list_modules", "Modules créés par l'entreprise (nom, identifiant)."), NoArgs, list_modules, "general"),
    ToolSpec(decl("get_module_definition", "Colonnes, statuts et indicateurs d'un module créé. À appeler avant d'ajouter "
                  "un enregistrement pour connaître les clés des colonnes.", _SLUG, ["slug"]),
             SlugArgs, get_module_definition, "general"),
    ToolSpec(decl("get_module_records", "Enregistrements d'un module créé.", {**_SLUG, "search": string("Recherche."),
                                                                             "status": string("Clé de statut.")},
                  ["slug"]), RecordsArgs, get_module_records, "general"),
    ToolSpec(decl("get_module_indicators", "Indicateurs du jour d'un module créé.", _SLUG, ["slug"]),
             SlugArgs, get_module_indicators, "general"),
    ToolSpec(decl("add_module_record", "Ajoute un enregistrement dans un module créé. values : clé de colonne vers "
                  "valeur." + WRITE, {**_SLUG, "values": S(type=T.OBJECT, description="Valeurs par clé de colonne.")},
                  ["slug", "values"]), NewRecordArgs, add_module_record, "general",
             lambda a: f"Ajouter un enregistrement au module « {a.slug} » : {a.values}."),
    ToolSpec(decl("advance_module_record", "Fait passer un enregistrement au statut suivant (définitif)." + WRITE,
                  {**_SLUG, "record_id": integer("Id de l'enregistrement.")}, ["slug", "record_id"]),
             AdvanceArgs, advance_module_record, "general",
             lambda a: f"Passer l'enregistrement #{a.record_id} du module « {a.slug} » au statut suivant."),
]
