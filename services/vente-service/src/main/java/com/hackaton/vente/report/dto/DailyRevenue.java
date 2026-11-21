package com.hackaton.vente.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyRevenue(LocalDate date, long salesCount, BigDecimal revenue) {
}
