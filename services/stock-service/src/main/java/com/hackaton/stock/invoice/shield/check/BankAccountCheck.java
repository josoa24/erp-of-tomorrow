package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.common.util.BankAccounts;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import com.hackaton.stock.supplier.entity.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Fraude au changement de coordonnées bancaires : le compte de la facture doit être celui du référentiel
 * fournisseur. Une annonce de changement de RIB dans la facture est signalée.
 */
@Component
public class BankAccountCheck implements InvoiceCheck {

    private static final Pattern CHANGE_NOTICE = Pattern.compile(
            "(nouveau|nouvelles?|changement|modification|new|updated?)\\W+(de\\W+|des\\W+)?(rib|iban|compte|coordonn|bank)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        List<ShieldAlert> alerts = new ArrayList<>();
        String invoiceAccount = BankAccounts.clean(context.invoice().bankAccount());
        String reference = context.supplier().map(Supplier::getBankAccount).map(BankAccounts::clean).orElse(null);
        if (invoiceAccount != null && reference != null && !BankAccounts.sameAccount(invoiceAccount, reference)) {
            alerts.add(ShieldAlert.blocking(AlertCode.BANK_ACCOUNT_MISMATCH,
                    "Compte de paiement différent de celui du fournisseur (facture …" + BankAccounts.last4(invoiceAccount)
                            + ", référentiel …" + BankAccounts.last4(reference) + ")"));
        } else if (invoiceAccount != null && reference == null && context.supplier().isPresent()) {
            alerts.add(ShieldAlert.warning(AlertCode.BANK_ACCOUNT_UNVERIFIED,
                    "Aucun compte de référence pour ce fournisseur : coordonnées à confirmer par un autre canal"));
        }
        String notes = context.invoice().notes();
        if (notes != null && CHANGE_NOTICE.matcher(notes).find()) {
            alerts.add(ShieldAlert.warning(AlertCode.BANK_ACCOUNT_UNVERIFIED,
                    "La facture annonce un changement de coordonnées bancaires : confirmer par téléphone"));
        }
        return alerts;
    }
}
