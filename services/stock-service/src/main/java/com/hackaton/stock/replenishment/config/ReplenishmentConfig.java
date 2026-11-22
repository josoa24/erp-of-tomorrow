package com.hackaton.stock.replenishment.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ReplenishmentProperties.class)
public class ReplenishmentConfig {
}
