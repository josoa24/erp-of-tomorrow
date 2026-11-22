package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Date future (bloquant), facture de plus d'un an ou antérieure à sa commande (à vérifier). */
@Component
public class InvoiceDateCheck implements InvoiceCheck {

    static final int MAX_AGE_DAYS = 365;

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        LocalDate date = context.invoice().invoiceDate();
        List<ShieldAlert> alerts = new ArrayList<>();
        if (date.isAfter(context.today())) {
            alerts.add(ShieldAlert.blocking(AlertCode.INVALID_DATE, "Facture datée dans le futur (" + date + ")"));
        } else if (date.isBefore(context.today().minusDays(MAX_AGE_DAYS))) {
            alerts.add(ShieldAlert.warning(AlertCode.INVALID_DATE, "Facture de plus d'un an (" + date + ")"));
        }
        context.purchaseOrder().ifPresent(order -> {
            LocalDate ordered = LocalDate.ofInstant(order.getCreatedAt(), ZoneId.systemDefault());
            if (date.isBefore(ordered)) {
                alerts.add(ShieldAlert.warning(AlertCode.INVALID_DATE,
                        "Facture antérieure au bon de commande (" + ordered + ")"));
            }
        });
        return alerts;
    }
}
