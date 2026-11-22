package com.hackaton.vente.common.time;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    @Bean
    public Clock appClock(@Value("${app.zone}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}
