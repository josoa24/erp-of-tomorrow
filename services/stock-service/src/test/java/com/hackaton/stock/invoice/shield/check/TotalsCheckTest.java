package com.hackaton.stock.invoice.shield.check;

import static com.hackaton.stock.invoice.InvoiceFixtures.ACCOUNT;
import static com.hackaton.stock.invoice.InvoiceFixtures.TODAY;
import static com.hackaton.stock.invoice.InvoiceFixtures.context;
import static com.hackaton.stock.invoice.InvoiceFixtures.invoice;
import static com.hackaton.stock.invoice.InvoiceFixtures.line;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.shield.AlertCode;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class TotalsCheckTest {

    private final TotalsCheck check = new TotalsCheck();

    @Test
    void shouldAccept_whenTotalsAreConsistent() {
        assertTrue(check.check(context(invoice())).isEmpty());
    }

    @Test
    void shouldAccept_whenSubtotalAndTaxAreMissingButLinesMatchTotal() {
        assertTrue(check.check(context(request(null, null, "1200000"))).isEmpty());
    }

    @Test
    void shouldTolerateOneAriaryRounding() {
        assertTrue(check.check(context(request("1200000", "240000", "1440001"))).isEmpty());
    }

    @Test
    void shouldBlock_whenLinesDoNotMatchSubtotal() {
        List<ShieldAlert> alerts = check.check(context(request("1300000", "260000", "1560000")));

        assertEquals(1, alerts.size());
        assertEquals(AlertCode.TOTALS_MISMATCH, alerts.get(0).code());
    }

    @Test
    void shouldBlock_whenTotalIsInflated() {
        List<ShieldAlert> alerts = check.check(context(request("1200000", "240000", "1940000")));

        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).message().contains("1940000"));
    }

    private SupplierInvoiceRequest request(String subtotal, String tax, String total) {
        return new SupplierInvoiceRequest(1L, "FA-1", TODAY, null, ACCOUNT, List.of(line(1L, 10, "120000")),
                subtotal == null ? null : new BigDecimal(subtotal), tax == null ? null : new BigDecimal(tax),
                new BigDecimal(total), null);
    }
}
