package com.hackaton.stock.invoice.shield.check;

import static com.hackaton.stock.invoice.InvoiceFixtures.ACCOUNT;
import static com.hackaton.stock.invoice.InvoiceFixtures.context;
import static com.hackaton.stock.invoice.InvoiceFixtures.invoice;
import static com.hackaton.stock.invoice.InvoiceFixtures.order;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.shield.AlertSeverity;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import java.util.List;
import org.junit.jupiter.api.Test;

class PurchaseOrderMatchCheckTest {

    private final PurchaseOrderMatchCheck check = new PurchaseOrderMatchCheck();

    @Test
    void shouldSkip_whenNoPurchaseOrderIsReferenced() {
        assertTrue(check.check(context(invoice())).isEmpty());
    }

    @Test
    void shouldAccept_whenInvoiceMatchesValidatedOrder() {
        assertTrue(run(order(OrderStatus.RECEIVED, 10, "120000")).isEmpty());
    }

    @Test
    void shouldBlock_whenReferencedOrderDoesNotExist() {
        List<ShieldAlert> alerts = check.check(context(invoice(ACCOUNT, 3L, null)));

        assertEquals(AlertSeverity.BLOCKING, alerts.get(0).severity());
    }

    @Test
    void shouldBlock_whenOrderIsStillDraft() {
        assertEquals(AlertSeverity.BLOCKING, run(order(OrderStatus.DRAFT, 10, "120000")).get(0).severity());
    }

    @Test
    void shouldBlock_whenOrderBelongsToAnotherSupplier() {
        PurchaseOrder order = order(OrderStatus.VALIDATED, 10, "120000");
        order.setSupplierId(2L);

        assertEquals(AlertSeverity.BLOCKING, run(order).get(0).severity());
    }

    @Test
    void shouldBlock_whenProductWasNotOrdered() {
        PurchaseOrder order = order(OrderStatus.VALIDATED, 10, "120000");
        order.getLines().get(0).setProductId(9L);

        assertEquals(AlertSeverity.BLOCKING, run(order).get(0).severity());
    }

    @Test
    void shouldWarn_whenInvoicedPriceExceedsOrderedPrice() {
        List<ShieldAlert> alerts = run(order(OrderStatus.VALIDATED, 10, "100000"));

        assertEquals(AlertSeverity.WARNING, alerts.get(0).severity());
    }

    @Test
    void shouldTolerateSmallPriceDifference() {
        assertTrue(run(order(OrderStatus.VALIDATED, 10, "118000")).isEmpty());
    }

    private List<ShieldAlert> run(PurchaseOrder order) {
        return check.check(context(invoice(ACCOUNT, 3L, null), order));
    }
}
