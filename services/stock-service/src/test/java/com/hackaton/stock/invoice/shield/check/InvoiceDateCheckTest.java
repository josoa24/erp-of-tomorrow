package com.hackaton.stock.invoice.shield.check;

import static com.hackaton.stock.invoice.InvoiceFixtures.ACCOUNT;
import static com.hackaton.stock.invoice.InvoiceFixtures.TODAY;
import static com.hackaton.stock.invoice.InvoiceFixtures.context;
import static com.hackaton.stock.invoice.InvoiceFixtures.line;
import static com.hackaton.stock.invoice.InvoiceFixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.shield.AlertSeverity;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvoiceDateCheckTest {

    private final InvoiceDateCheck check = new InvoiceDateCheck();

    @Test
    void shouldAccept_whenDateIsRecent() {
        assertTrue(check.check(context(dated(TODAY.minusDays(3)))).isEmpty());
    }

    @Test
    void shouldBlock_whenDateIsInTheFuture() {
        List<ShieldAlert> alerts = check.check(context(dated(TODAY.plusDays(1))));

        assertEquals(AlertSeverity.BLOCKING, alerts.get(0).severity());
    }

    @Test
    void shouldWarn_whenInvoiceIsOlderThanOneYear() {
        List<ShieldAlert> alerts = check.check(context(dated(TODAY.minusDays(400))));

        assertEquals(AlertSeverity.WARNING, alerts.get(0).severity());
    }

    @Test
    void shouldWarn_whenInvoicePredatesItsPurchaseOrder() {
        List<ShieldAlert> alerts = check.check(context(dated(LocalDate.of(2026, 11, 1)),
                order(OrderStatus.RECEIVED, 10, "120000")));

        assertEquals(1, alerts.size());
        assertEquals(AlertSeverity.WARNING, alerts.get(0).severity());
    }

    private SupplierInvoiceRequest dated(LocalDate date) {
        return new SupplierInvoiceRequest(1L, "FA-1", date, 3L, ACCOUNT, List.of(line(1L, 1, "10")),
                null, null, BigDecimal.TEN, null);
    }
}
