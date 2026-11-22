package com.hackaton.stock.replenishment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "replenishment")
public record ReplenishmentProperties(
        @DefaultValue("7") int defaultLeadDays,
        @DefaultValue("30") int coverageDays) {
}
