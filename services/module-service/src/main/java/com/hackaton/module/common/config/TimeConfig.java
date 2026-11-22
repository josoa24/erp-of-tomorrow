package com.hackaton.module.common.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    @Bean
    public Clock moduleClock(@Value("${module.time-zone}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}
