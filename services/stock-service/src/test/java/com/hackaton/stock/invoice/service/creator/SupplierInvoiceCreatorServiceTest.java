package com.hackaton.stock.invoice.service.creator;

import static com.hackaton.stock.invoice.InvoiceFixtures.invoice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.entity.InvoiceEventType;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.mapper.SupplierInvoiceMapper;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.service.audit.InvoiceAuditTrail;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceShield;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierInvoiceCreatorServiceTest {

    private static final Actor CLERK = new Actor(5L, "stock@erp.local", "STOCK");

    @Mock
    private InvoiceShield invoiceShield;
    @Mock
    private SupplierInvoiceRepository invoiceRepository;
    @Mock
    private InvoiceAuditTrail auditTrail;

    private SupplierInvoiceCreatorService service;

    @BeforeEach
    void setUp() {
        service = new SupplierInvoiceCreatorService(invoiceShield, invoiceRepository, new SupplierInvoiceMapper(), auditTrail);
        when(invoiceRepository.save(any(SupplierInvoice.class))).thenAnswer(call -> {
            SupplierInvoice saved = call.getArgument(0);
            saved.setId(12L);
            return saved;
        });
    }

    @Test
    void shouldSaveAsCleared_andMaskBankAccount_whenNothingIsBlocking() {
        when(invoiceShield.inspect(any())).thenReturn(ShieldReport.of(List.of()));

        SupplierInvoiceResponse response = service.create(invoice(), CLERK);

        assertEquals(InvoiceStatus.CLEARED, response.status());
        assertEquals("2345", response.bankAccountLast4());
        assertEquals(5L, response.createdBy());
        verify(auditTrail).record(eq(12L), eq(InvoiceEventType.CREATED), eq(CLERK), any());
        verify(auditTrail).record(12L, InvoiceEventType.CHECKED, CLERK, "Verdict CLEAR");
    }

    @Test
    void shouldStillSaveAsBlocked_andTraceAlertCodes_whenShieldBlocks() {
        when(invoiceShield.inspect(any())).thenReturn(ShieldReport.of(List.of(
                ShieldAlert.blocking(AlertCode.BANK_ACCOUNT_MISMATCH, "compte"),
                ShieldAlert.blocking(AlertCode.PROMPT_INJECTION, "consigne"))));

        SupplierInvoiceResponse response = service.create(invoice(), CLERK);

        assertEquals(InvoiceStatus.BLOCKED, response.status());
        assertEquals(2, response.alerts().size());
        verify(auditTrail).record(12L, InvoiceEventType.CHECKED, CLERK,
                "Verdict BLOCKED : BANK_ACCOUNT_MISMATCH, PROMPT_INJECTION");
    }

    @Test
    void shouldNeverWriteBankAccountInAuditTrail() {
        when(invoiceShield.inspect(any())).thenReturn(ShieldReport.of(List.of()));

        service.create(invoice(), CLERK);

        verify(auditTrail).record(eq(12L), eq(InvoiceEventType.CREATED), eq(CLERK),
                org.mockito.ArgumentMatchers.argThat(detail -> {
                    assertFalse(detail.contains("MG46"));
                    return true;
                }));
    }
}
