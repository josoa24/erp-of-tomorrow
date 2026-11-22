package com.hackaton.stock.invoice.service.decision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.ForbiddenException;
import com.hackaton.stock.common.exception.ImmutableResourceException;
import com.hackaton.stock.common.exception.InvalidRequestException;
import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.entity.InvoiceEventType;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.mapper.SupplierInvoiceMapper;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.service.audit.InvoiceAuditTrail;
import com.hackaton.stock.invoice.shield.ShieldVerdict;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvoiceDeciderServiceTest {

    private static final Actor CLERK = new Actor(5L, "stock@erp.local", "STOCK");
    private static final Actor ADMIN = new Actor(1L, "admin@erp.local", "ADMIN");

    @Mock
    private SupplierInvoiceRepository invoiceRepository;
    @Mock
    private InvoiceAuditTrail auditTrail;

    private InvoiceDeciderService service;
    private final SupplierInvoice invoice = new SupplierInvoice();

    @BeforeEach
    void setUp() {
        service = new InvoiceDeciderService(invoiceRepository, new SupplierInvoiceMapper(), auditTrail);
        invoice.setId(12L);
        invoice.setCreatedBy(5L);
        invoice.setTotalAmount(BigDecimal.TEN);
        invoice.setVerdict(ShieldVerdict.CLEAR);
        invoice.setStatus(InvoiceStatus.CLEARED);
        when(invoiceRepository.findById(12L)).thenReturn(Optional.of(invoice));
    }

    @Test
    void shouldApproveClearedInvoice_forStockRole() {
        assertEquals(InvoiceStatus.APPROVED, service.approve(12L, null, CLERK).status());
        verify(auditTrail).record(12L, InvoiceEventType.APPROVED, CLERK, null);
    }

    @Test
    void shouldRefuseOverride_whenUserIsNotAdmin() {
        invoice.setStatus(InvoiceStatus.BLOCKED);

        assertThrows(ForbiddenException.class, () -> service.approve(12L, "vérifié", new Actor(7L, null, "STOCK")));
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void shouldRefuseOverride_whenAdminIsTheInvoiceAuthor() {
        invoice.setStatus(InvoiceStatus.BLOCKED);
        invoice.setCreatedBy(1L);

        assertThrows(ForbiddenException.class, () -> service.approve(12L, "vérifié", ADMIN));
    }

    @Test
    void shouldRequireJustification_whenAdminOverridesBlockedInvoice() {
        invoice.setStatus(InvoiceStatus.BLOCKED);

        assertThrows(InvalidRequestException.class, () -> service.approve(12L, " ", ADMIN));
    }

    @Test
    void shouldOverrideAndTraceJustification_whenAdminConfirms() {
        invoice.setStatus(InvoiceStatus.BLOCKED);

        var response = service.approve(12L, "Nouveau RIB confirmé par téléphone", ADMIN);

        assertEquals(InvoiceStatus.APPROVED, response.status());
        assertEquals(1L, response.decidedBy());
        verify(auditTrail).record(12L, InvoiceEventType.OVERRIDDEN, ADMIN, "Nouveau RIB confirmé par téléphone");
    }

    @Test
    void shouldReject_andKeepReason() {
        var response = service.reject(12L, "Doublon", CLERK);

        assertEquals(InvoiceStatus.REJECTED, response.status());
        assertEquals("Doublon", response.decisionNote());
    }

    @Test
    void shouldRefuseAnyNewDecision_onceInvoiceIsDecided() {
        invoice.setStatus(InvoiceStatus.APPROVED);

        assertThrows(ImmutableResourceException.class, () -> service.reject(12L, "trop tard", ADMIN));
        assertThrows(ImmutableResourceException.class, () -> service.approve(12L, null, ADMIN));
    }
}
