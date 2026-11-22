package com.hackaton.rh.common.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock(@Value("${app.zone:Indian/Antananarivo}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }

    @Bean
    public ValidationConfigurationCustomizer localValidationClock(Clock clock) {
        return configuration -> configuration.clockProvider(() -> clock);
    }
}
