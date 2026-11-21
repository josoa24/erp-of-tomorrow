package com.hackaton.stock.invoice.shield.check;

import static com.hackaton.stock.invoice.InvoiceFixtures.ACCOUNT;
import static com.hackaton.stock.invoice.InvoiceFixtures.context;
import static com.hackaton.stock.invoice.InvoiceFixtures.invoice;
import static com.hackaton.stock.invoice.InvoiceFixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.entity.SupplierInvoiceLine;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.AlertSeverity;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Contrôles qui s'appuient sur l'historique des factures : doublons et surfacturation. */
@ExtendWith(MockitoExtension.class)
class RepositoryChecksTest {

    @Mock
    private SupplierInvoiceRepository invoiceRepository;

    @Test
    void shouldBlockDuplicate_whenSameNumberWrittenDifferently() {
        when(invoiceRepository.existsBySupplierIdAndInvoiceNumberKeyAndStatusNot(1L, "FA20260042", InvoiceStatus.REJECTED))
                .thenReturn(true);

        var alerts = new DuplicateInvoiceCheck(invoiceRepository).check(context(invoice()));

        assertEquals(AlertSeverity.BLOCKING, alerts.get(0).severity());
        assertEquals(AlertCode.DUPLICATE_INVOICE, alerts.get(0).code());
    }

    @Test
    void shouldWarnDuplicate_whenSameAmountAndDateUnderAnotherNumber() {
        when(invoiceRepository.existsBySupplierIdAndTotalAmountAndInvoiceDateAndStatusNot(
                eq(1L), any(), any(), eq(InvoiceStatus.REJECTED))).thenReturn(true);

        var alerts = new DuplicateInvoiceCheck(invoiceRepository).check(context(invoice()));

        assertEquals(AlertSeverity.WARNING, alerts.get(0).severity());
    }

    @Test
    void shouldBlockOverInvoicing_whenPreviousInvoicesAlreadyCoverTheOrder() {
        when(invoiceRepository.findByPurchaseOrderIdAndStatusNot(3L, InvoiceStatus.REJECTED))
                .thenReturn(List.of(previousInvoice(6)));

        var alerts = new OverInvoicingCheck(invoiceRepository)
                .check(context(invoice(ACCOUNT, 3L, null), order(OrderStatus.RECEIVED, 12, "120000")));

        assertEquals(AlertCode.OVER_INVOICED, alerts.get(0).code());
        assertTrue(alerts.get(0).message().contains("16"));
    }

    @Test
    void shouldAcceptPartialInvoices_whenTotalStaysWithinOrder() {
        when(invoiceRepository.findByPurchaseOrderIdAndStatusNot(3L, InvoiceStatus.REJECTED))
                .thenReturn(List.of(previousInvoice(2)));

        assertTrue(new OverInvoicingCheck(invoiceRepository)
                .check(context(invoice(ACCOUNT, 3L, null), order(OrderStatus.RECEIVED, 12, "120000"))).isEmpty());
    }

    private static SupplierInvoice previousInvoice(int quantity) {
        SupplierInvoiceLine line = new SupplierInvoiceLine();
        line.setProductId(1L);
        line.setQuantity(quantity);
        SupplierInvoice invoice = new SupplierInvoice();
        invoice.getLines().add(line);
        return invoice;
    }
}
