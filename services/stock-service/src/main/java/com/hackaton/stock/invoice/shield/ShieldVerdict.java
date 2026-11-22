package com.hackaton.stock.invoice.shield;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import java.util.List;

public enum ShieldVerdict {
    CLEAR,
    WARNING,
    BLOCKED;

    public static ShieldVerdict of(List<ShieldAlert> alerts) {
        if (alerts.stream().anyMatch(alert -> alert.severity() == AlertSeverity.BLOCKING)) {
            return BLOCKED;
        }
        return alerts.isEmpty() ? CLEAR : WARNING;
    }
}
