package com.hackaton.stock.invoice.shield.check;

import static com.hackaton.stock.invoice.InvoiceFixtures.ACCOUNT;
import static com.hackaton.stock.invoice.InvoiceFixtures.TODAY;
import static com.hackaton.stock.invoice.InvoiceFixtures.invoice;
import static com.hackaton.stock.invoice.InvoiceFixtures.supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.AlertSeverity;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class BankAccountCheckTest {

    private final BankAccountCheck check = new BankAccountCheck();

    @Test
    void shouldAcceptSameAccount_whenOnlySpacingDiffers() {
        assertTrue(run(invoice("mg4600005000123456789012345", null, null), ACCOUNT).isEmpty());
    }

    @Test
    void shouldBlockAndMaskAccounts_whenAccountDiffersFromReference() {
        List<ShieldAlert> alerts = run(invoice("MG46 9999 0000 1111 2222 3333 999", null, null), ACCOUNT);

        assertEquals(AlertCode.BANK_ACCOUNT_MISMATCH, alerts.get(0).code());
        assertEquals(AlertSeverity.BLOCKING, alerts.get(0).severity());
        assertFalse(alerts.get(0).message().contains("9999 0000"));
        assertTrue(alerts.get(0).message().contains("3999"));
    }

    @Test
    void shouldWarn_whenSupplierHasNoReferenceAccount() {
        List<ShieldAlert> alerts = run(invoice(), null);

        assertEquals(AlertSeverity.WARNING, alerts.get(0).severity());
        assertEquals(AlertCode.BANK_ACCOUNT_UNVERIFIED, alerts.get(0).code());
    }

    @Test
    void shouldWarn_whenInvoiceAnnouncesNewBankDetails() {
        List<ShieldAlert> alerts = run(invoice(ACCOUNT, null, "Attention : nouveau RIB à compter de ce mois"), ACCOUNT);

        assertEquals(1, alerts.size());
        assertEquals(AlertCode.BANK_ACCOUNT_UNVERIFIED, alerts.get(0).code());
    }

    private List<ShieldAlert> run(SupplierInvoiceRequest request, String referenceAccount) {
        return check.check(new InvoiceCheckContext(request, Optional.of(supplier(referenceAccount)), Optional.empty(), TODAY));
    }
}
