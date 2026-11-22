package com.hackaton.stock.invoice.shield;

import static com.hackaton.stock.invoice.InvoiceFixtures.ACCOUNT;
import static com.hackaton.stock.invoice.InvoiceFixtures.invoice;
import static com.hackaton.stock.invoice.InvoiceFixtures.supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.shield.check.SupplierExistenceCheck;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvoiceShieldServiceTest {

    private static final Clock CLOCK = Clock.fixed(java.time.Instant.parse("2026-11-22T06:00:00Z"), ZoneOffset.UTC);

    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private PurchaseOrderRepository orderRepository;

    @Test
    void shouldReturnClear_whenNoCheckRaisesAlert() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier(ACCOUNT)));

        ShieldReport report = shield(List.of(new SupplierExistenceCheck())).inspect(invoice());

        assertEquals(ShieldVerdict.CLEAR, report.verdict());
        verify(orderRepository, never()).findById(ArgumentMatchers.any());
    }

    @Test
    void shouldReturnBlocked_whenSupplierIsUnknown() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        ShieldReport report = shield(List.of(new SupplierExistenceCheck())).inspect(invoice());

        assertEquals(ShieldVerdict.BLOCKED, report.verdict());
        assertEquals(AlertCode.UNKNOWN_SUPPLIER, report.alerts().get(0).code());
    }

    @Test
    void shouldCombineAlertsAndKeepWarningVerdict_whenNoAlertIsBlocking() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier(ACCOUNT)));
        when(orderRepository.findById(3L)).thenReturn(Optional.empty());
        InvoiceCheck warnOnce = context -> List.of(ShieldAlert.warning(AlertCode.AMOUNT_ANOMALY, "a"));

        ShieldReport report = shield(List.of(warnOnce, warnOnce)).inspect(invoice(ACCOUNT, 3L, null));

        assertEquals(ShieldVerdict.WARNING, report.verdict());
        assertEquals(2, report.alerts().size());
    }

    @Test
    void shouldPassTodayFromClock_toChecks() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier(ACCOUNT)));
        InvoiceCheck captureDate = context -> List.of(
                ShieldAlert.warning(AlertCode.INVALID_DATE, context.today().toString()));

        ShieldReport report = shield(List.of(captureDate)).inspect(invoice());

        assertEquals("2026-11-22", report.alerts().get(0).message());
    }

    private InvoiceShieldService shield(List<InvoiceCheck> checks) {
        return new InvoiceShieldService(checks, supplierRepository, orderRepository, CLOCK);
    }
}
