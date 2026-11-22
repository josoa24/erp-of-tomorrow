from __future__ import annotations

from typing import Literal

from pydantic import Field

from app.erp.client import ErpClient, keep
from app.tools.spec import WRITE, Args, NoArgs, S, T, ToolSpec, decl, integer, string

INTERACTION_TYPES = ["APPEL", "EMAIL", "REUNION", "VISITE", "NOTE"]


class ContactsArgs(Args):
    kind: Literal["ALL", "CUSTOMER", "SUPPLIER"] = "ALL"
    search: str | None = Field(default=None, max_length=100)
    follow_up: bool = False
    professional: bool = False


class InteractionArgs(Args):
    party_type: Literal["CUSTOMER", "SUPPLIER"]
    party_id: int = Field(ge=1)
    type: Literal["APPEL", "EMAIL", "REUNION", "VISITE", "NOTE"]
    summary: str = Field(min_length=1, max_length=2000)


class NewContactArgs(Args):
    kind: Literal["CUSTOMER", "SUPPLIER"]
    name: str = Field(min_length=1, max_length=255)
    phone: str | None = Field(default=None, max_length=40)
    email: str | None = Field(default=None, max_length=255)
    city: str | None = Field(default=None, max_length=80)
    professional: bool = False


async def list_contacts(erp: ErpClient, a: ContactsArgs):
    params = {"kind": a.kind, "search": a.search, "followUp": a.follow_up or None,
              "professional": a.professional or None}
    return keep(await erp.call("GET", "/api/crm/contacts", params=params), 30)


async def get_contact_summary(erp: ErpClient, a: NoArgs):
    return await erp.call("GET", "/api/crm/contacts/summary")


async def record_interaction(erp: ErpClient, a: InteractionArgs):
    body = {"type": a.type, "summary": a.summary}
    return await erp.call("POST", f"/api/crm/parties/{a.party_type}/{a.party_id}/interactions", json=body)


async def create_contact(erp: ErpClient, a: NewContactArgs):
    body = {"kind": a.kind, "name": a.name, "phone": a.phone, "email": a.email, "city": a.city}
    if a.kind == "CUSTOMER":
        body["type"] = "ENTREPRISE" if a.professional else "PARTICULIER"
    return await erp.call("POST", "/api/crm/contacts", json={k: v for k, v in body.items() if v is not None})


_PARTY = {"party_type": string("Type de tiers.", ["CUSTOMER", "SUPPLIER"]), "party_id": integer("Id du tiers.")}

SPECS = [
    ToolSpec(decl("list_contacts", "CRM : clients et fournisseurs avec dernier échange, statut (ACTIVE ou "
                  "FOLLOW_UP = sans nouvelles depuis 30 jours), plafond de crédit et encours.",
                  {"kind": string("Type.", ["ALL", "CUSTOMER", "SUPPLIER"]), "search": string("Nom, ville, téléphone."),
                   "follow_up": S(type=T.BOOLEAN, description="true = seulement les contacts à relancer"),
                   "professional": S(type=T.BOOLEAN, description="true = clients professionnels")}),
             ContactsArgs, list_contacts, "crm"),
    ToolSpec(decl("get_contact_summary", "CRM : clients actifs du trimestre, fournisseurs, commandes en cours, "
                  "contacts à relancer, encours de crédit."), NoArgs, get_contact_summary, "crm"),
    ToolSpec(decl("record_interaction", "CRM : enregistre un échange avec un client ou un fournisseur (non "
                  "modifiable ensuite)." + WRITE, {**_PARTY, "type": string("Nature de l'échange.", INTERACTION_TYPES),
                                                    "summary": string("Résumé de l'échange.")},
                  ["party_type", "party_id", "type", "summary"]), InteractionArgs, record_interaction, "crm",
             lambda a: f"Enregistrer un échange ({a.type}) avec {a.party_type} #{a.party_id} : « {a.summary} »."),
    ToolSpec(decl("create_contact", "CRM : crée un client (ajouté aux Ventes) ou un fournisseur (ajouté au Stock)."
                  + WRITE, {"kind": string("Type de contact.", ["CUSTOMER", "SUPPLIER"]), "name": string("Nom."),
                            "phone": string("Téléphone."), "email": string("E-mail."), "city": string("Ville."),
                            "professional": S(type=T.BOOLEAN, description="Client professionnel (sinon particulier)")},
                  ["kind", "name"]), NewContactArgs, create_contact, "crm",
             lambda a: f"Créer le {'client' if a.kind == 'CUSTOMER' else 'fournisseur'} « {a.name} »"
                       f"{' à ' + a.city if a.city else ''}."),
]
