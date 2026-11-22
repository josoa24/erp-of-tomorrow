package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.replenishment.dto.LeadTimeSource;
import java.util.ArrayList;
import java.util.List;

/** Phrases courtes qui expliquent d'où viennent les chiffres d'une prévision. */
public final class ReplenishmentAssumptions {

    private ReplenishmentAssumptions() {
    }

    public static List<String> describe(int analysisDays, LeadTime lead, boolean enoughHistory, int coverageDays) {
        List<String> sentences = new ArrayList<>();
        sentences.add("Consommation calculée sur les sorties des " + analysisDays
                + " derniers jours, jours sans sortie comptés à 0.");
        sentences.add("Niveau de service visé : 95 % (stock de sécurité = 1,65 × écart-type × √délai).");
        sentences.add(leadSentence(lead));
        sentences.add("Quantité recommandée pour couvrir le délai plus " + coverageDays + " jours de consommation.");
        if (!enoughHistory) {
            sentences.add("Moins de " + ReplenishmentInputLoader.MIN_HISTORY_DAYS
                    + " jours d'historique : aucune quantité recommandée.");
        }
        return List.copyOf(sentences);
    }

    private static String leadSentence(LeadTime lead) {
        String prefix = "Délai de livraison de " + lead.days() + " jours : ";
        if (lead.source() == LeadTimeSource.SUPPLIER) {
            return prefix + "délai habituel du fournisseur " + lead.supplierId()
                    + " (dernier bon de commande contenant ce produit).";
        }
        if (lead.supplierId() != null) {
            return prefix + "valeur par défaut, le fournisseur " + lead.supplierId()
                    + " n'a pas de délai habituel renseigné.";
        }
        return prefix + "valeur par défaut, aucun bon de commande ne contient ce produit.";
    }
}
