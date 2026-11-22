package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import com.hackaton.stock.invoice.shield.InvoiceNumbers;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Double paiement : même numéro chez le même fournisseur (bloquant), ou même montant à la même date sous un
 * autre numéro (à vérifier). Les factures rejetées ne comptent pas : on peut les ressoumettre corrigées.
 */
@Component
@RequiredArgsConstructor
public class DuplicateInvoiceCheck implements InvoiceCheck {

    private final SupplierInvoiceRepository invoiceRepository;

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        SupplierInvoiceRequest invoice = context.invoice();
        String key = InvoiceNumbers.key(invoice.invoiceNumber());
        if (invoiceRepository.existsBySupplierIdAndInvoiceNumberKeyAndStatusNot(
                invoice.supplierId(), key, InvoiceStatus.REJECTED)) {
            return List.of(ShieldAlert.blocking(AlertCode.DUPLICATE_INVOICE,
                    "La facture " + invoice.invoiceNumber() + " de ce fournisseur est déjà enregistrée"));
        }
        if (invoiceRepository.existsBySupplierIdAndTotalAmountAndInvoiceDateAndStatusNot(
                invoice.supplierId(), invoice.totalAmount(), invoice.invoiceDate(), InvoiceStatus.REJECTED)) {
            return List.of(ShieldAlert.warning(AlertCode.DUPLICATE_INVOICE,
                    "Une autre facture de ce fournisseur a le même montant à la même date"));
        }
        return List.of();
    }
}
