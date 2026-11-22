package com.hackaton.stock.invoice.dto;

import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.AlertSeverity;

public record ShieldAlert(AlertCode code, AlertSeverity severity, String message) {

    public static ShieldAlert blocking(AlertCode code, String message) {
        return new ShieldAlert(code, AlertSeverity.BLOCKING, message);
    }

    public static ShieldAlert warning(AlertCode code, String message) {
        return new ShieldAlert(code, AlertSeverity.WARNING, message);
    }
}
