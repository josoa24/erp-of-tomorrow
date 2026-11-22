package com.hackaton.stock.lot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "stock")
public record ExpiryProperties(@DefaultValue("30") int expiryAlertDays) {
}
