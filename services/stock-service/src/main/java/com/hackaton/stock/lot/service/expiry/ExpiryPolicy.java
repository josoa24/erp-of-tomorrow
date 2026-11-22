package com.hackaton.stock.lot.service.expiry;

import com.hackaton.stock.lot.config.ExpiryProperties;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpiryPolicy {

    private final Clock clock;
    private final ExpiryProperties properties;

    public Integer daysToExpiry(LocalDate expiryDate) {
        if (expiryDate == null) {
            return null;
        }
        return Math.toIntExact(ChronoUnit.DAYS.between(LocalDate.now(clock), expiryDate));
    }

    public boolean isExpiringSoon(Integer daysToExpiry) {
        return daysToExpiry != null && daysToExpiry <= properties.expiryAlertDays();
    }
}
