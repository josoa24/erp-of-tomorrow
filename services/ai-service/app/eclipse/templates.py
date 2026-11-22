from __future__ import annotations

from app.eclipse.template_parts import ModuleTemplate, column, definition, indicator, status
from app.eclipse.text import any_of, normalize

LIVRAISONS = ModuleTemplate(
    any_of("livraison", "livreur", r"livr(?:er|ez|ons|ent|ee|ees)\b", "tournee", "colis", "expedi"),
    definition("Livraisons", "truck", [
        column("client", "Client", "REFERENCE", "CUSTOMER"),
        column("vente_origine", "Vente d’origine", "REFERENCE", "SALE", listed=False),
        column("quartier", "Quartier", "TEXT"),
        column("livreur", "Livreur", "REFERENCE", "EMPLOYEE"),
        column("heure_prevue", "Heure prévue", "TIME"),
        column("montant_a_encaisser", "Montant à encaisser", "MONEY", listed=False),
        column("signature_client", "Signature du client", "TEXT", listed=False),
    ], [
        status("a_preparer", "À préparer", "NEUTRAL"),
        status("en_route", "En route", "INFO"),
        status("livree", "Livrée", "SUCCESS", final=True),
    ], [
        indicator("livrees_aujourdhui", "Livrées aujourd’hui", "truck", "RATIO_TODAY", statuses=["livree"],
                  todayColumn="heure_prevue"),
        indicator("en_route", "En route", "clock", "COUNT", statuses=["en_route"], detailColumn="livreur"),
        indicator("a_encaisser", "À encaisser", "receipt", "SUM", statuses=["a_preparer", "en_route"],
                  column="montant_a_encaisser"),
    ]),
    unchecked_columns=("signature_client",),
)

REPARATIONS = ModuleTemplate(
    any_of("repar", "panne", "depann", r"sav\b", "atelier", "technicien"),
    definition("Réparations", "wrench", [
        column("client", "Client", "REFERENCE", "CUSTOMER"),
        column("appareil", "Appareil", "TEXT", required=True),
        column("panne", "Panne constatée", "TEXT", listed=False),
        column("technicien", "Technicien", "REFERENCE", "EMPLOYEE"),
        column("date_promise", "Date promise", "DATE"),
        column("montant", "Montant", "MONEY", listed=False),
    ], [
        status("recue", "Reçue", "NEUTRAL"),
        status("en_reparation", "En réparation", "WARNING"),
        status("prete", "Prête", "INFO"),
        status("rendue", "Rendue", "SUCCESS", final=True),
    ], [
        indicator("en_reparation", "En réparation", "wrench", "COUNT", statuses=["en_reparation"],
                  detailColumn="technicien"),
        indicator("pretes", "Prêtes à rendre", "circle-check", "COUNT", statuses=["prete"]),
        indicator("a_encaisser", "À encaisser", "receipt", "SUM", statuses=["recue", "en_reparation", "prete"],
                  column="montant"),
    ]),
)

RENDEZ_VOUS = ModuleTemplate(
    any_of("rendez", r"rdv\b", "agenda", "reservation", "creneau", "consultation"),
    definition("Rendez-vous", "calendar-days", [
        column("client", "Client", "REFERENCE", "CUSTOMER"),
        column("date", "Date", "DATE", required=True),
        column("heure", "Heure", "TIME"),
        column("motif", "Motif", "TEXT", listed=False),
        column("avec", "Avec", "REFERENCE", "EMPLOYEE"),
    ], [
        status("prevu", "Prévu", "NEUTRAL"),
        status("confirme", "Confirmé", "INFO"),
        status("termine", "Terminé", "SUCCESS", final=True),
    ], [
        indicator("aujourdhui", "Aujourd’hui", "calendar-days", "RATIO_TODAY", statuses=["termine"],
                  todayColumn="date"),
        indicator("a_confirmer", "À confirmer", "clock", "COUNT", statuses=["prevu"]),
    ]),
)

TEMPLATES: tuple[ModuleTemplate, ...] = (LIVRAISONS, REPARATIONS, RENDEZ_VOUS)


def choose_template(description: str) -> ModuleTemplate | None:
    normalized = normalize(description)
    return next((template for template in TEMPLATES if template.pattern.search(normalized)), None)
