package com.hackaton.rh.advance.dto;

import java.math.BigDecimal;

public record AdvanceSummaryResponse(BigDecimal monthTotal, long requests, long pending) {
}
