package com.hackaton.vente.report.service;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import java.time.Instant;

final class ReportSales {

    private ReportSales() {
    }

    static Sale validated(Long id, String validatedAt, SaleLine... lines) {
        Sale sale = SaleFixtures.draftSale(id, lines);
        sale.setStatus(DocumentStatus.VALIDATED);
        sale.setValidatedAt(Instant.parse(validatedAt));
        return sale;
    }

    static Sale monday() {
        return validated(1L, "2026-11-16T10:00:00Z", SaleFixtures.line(10L, "Riz", 10, "1000"));
    }

    static Sale tuesday() {
        return validated(2L, "2026-11-17T10:00:00Z",
                SaleFixtures.line(10L, "Riz", 5, "1000"), SaleFixtures.line(20L, "Lait", 20, "100"));
    }
}
