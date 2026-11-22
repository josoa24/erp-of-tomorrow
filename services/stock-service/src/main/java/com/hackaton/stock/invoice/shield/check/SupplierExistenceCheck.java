package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import java.util.List;
import org.springframework.stereotype.Component;

/** Une facture d'un fournisseur inconnu ne doit jamais être payée sans vérification. */
@Component
public class SupplierExistenceCheck implements InvoiceCheck {

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        if (context.supplier().isPresent()) {
            return List.of();
        }
        return List.of(ShieldAlert.blocking(AlertCode.UNKNOWN_SUPPLIER,
                "Fournisseur " + context.invoice().supplierId() + " inconnu dans le référentiel"));
    }
}
