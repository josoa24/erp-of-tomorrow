package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.replenishment.dto.LeadTimeSource;

/** Délai de livraison retenu et fournisseur du dernier bon de commande contenant le produit (null si aucun). */
public record LeadTime(int days, LeadTimeSource source, Long supplierId) {
}
