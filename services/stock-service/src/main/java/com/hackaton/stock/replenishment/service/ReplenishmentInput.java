package com.hackaton.stock.replenishment.service;

import java.util.List;

/** Données brutes d'une prévision : sorties par jour calendaire, historique suffisant, délai. */
public record ReplenishmentInput(List<Long> dailyOuts, boolean enoughHistory, LeadTime leadTime) {
}
