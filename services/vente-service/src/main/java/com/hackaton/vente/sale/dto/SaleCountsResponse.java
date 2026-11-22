package com.hackaton.vente.sale.dto;

public record SaleCountsResponse(
        long all, long toCollect, long paid, long cancelled, long overdue, long credit, long draft) {
}
