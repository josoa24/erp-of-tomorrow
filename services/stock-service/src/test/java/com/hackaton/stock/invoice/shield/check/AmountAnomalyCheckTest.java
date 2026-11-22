package com.hackaton.stock.invoice.shield.check;

import static com.hackaton.stock.invoice.InvoiceFixtures.context;
import static com.hackaton.stock.invoice.InvoiceFixtures.invoice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.shield.AlertCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AmountAnomalyCheckTest {

    @Mock
    private SupplierInvoiceRepository invoiceRepository;

    @InjectMocks
    private AmountAnomalyCheck check;

    @Test
    void shouldSkip_whenHistoryIsTooShort() {
        history("100000", "120000", "110000", "90000");

        assertTrue(check.check(context(invoice())).isEmpty());
    }

    @Test
    void shouldWarn_whenAmountIsFarAboveUsualInvoices() {
        history("100000", "120000", "110000", "90000", "105000");

        var alerts = check.check(context(invoice()));

        assertEquals(AlertCode.AMOUNT_ANOMALY, alerts.get(0).code());
    }

    @Test
    void shouldAccept_whenAmountIsUsual() {
        history("1400000", "1500000", "1450000", "1380000", "1420000");

        assertTrue(check.check(context(invoice())).isEmpty());
    }

    @Test
    void shouldWarn_whenHistoryIsConstantAndAmountDoubles() {
        history("500000", "500000", "500000", "500000", "500000");

        assertEquals(1, check.check(context(invoice())).size());
    }

    private void history(String... totals) {
        List<SupplierInvoice> invoices = Stream.of(totals).map(total -> {
            SupplierInvoice invoice = new SupplierInvoice();
            invoice.setTotalAmount(new BigDecimal(total));
            return invoice;
        }).toList();
        when(invoiceRepository.findBySupplierIdAndStatusNot(1L, InvoiceStatus.REJECTED)).thenReturn(invoices);
    }
}
