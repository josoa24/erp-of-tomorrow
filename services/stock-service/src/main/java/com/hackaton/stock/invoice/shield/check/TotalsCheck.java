package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.InvoiceLineRequest;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/** Les totaux doivent découler des lignes : lignes = sous-total, sous-total + taxes = total (à 1 Ar près). */
@Component
public class TotalsCheck implements InvoiceCheck {

    static final BigDecimal TOLERANCE = BigDecimal.ONE;

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        SupplierInvoiceRequest invoice = context.invoice();
        BigDecimal linesTotal = invoice.lines().stream()
                .map(InvoiceLineRequest::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = invoice.taxAmount() == null ? BigDecimal.ZERO : invoice.taxAmount();
        BigDecimal subtotal = invoice.subtotal() == null ? linesTotal : invoice.subtotal();
        List<ShieldAlert> alerts = new ArrayList<>();
        if (differs(linesTotal, subtotal)) {
            alerts.add(ShieldAlert.blocking(AlertCode.TOTALS_MISMATCH,
                    "Somme des lignes " + plain(linesTotal) + " Ar différente du sous-total " + plain(subtotal) + " Ar"));
        }
        BigDecimal expectedTotal = subtotal.add(tax);
        if (differs(expectedTotal, invoice.totalAmount())) {
            alerts.add(ShieldAlert.blocking(AlertCode.TOTALS_MISMATCH,
                    "Total " + plain(invoice.totalAmount()) + " Ar différent de sous-total + taxes = "
                            + plain(expectedTotal) + " Ar"));
        }
        return alerts;
    }

    private static boolean differs(BigDecimal first, BigDecimal second) {
        return first.subtract(second).abs().compareTo(TOLERANCE) > 0;
    }

    private static String plain(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }
}
