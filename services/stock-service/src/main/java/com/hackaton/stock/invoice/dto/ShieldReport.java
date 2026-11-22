package com.hackaton.stock.invoice.dto;

import com.hackaton.stock.invoice.shield.ShieldVerdict;
import java.util.List;

public record ShieldReport(ShieldVerdict verdict, List<ShieldAlert> alerts) {

    public static ShieldReport of(List<ShieldAlert> alerts) {
        return new ShieldReport(ShieldVerdict.of(alerts), List.copyOf(alerts));
    }
}
