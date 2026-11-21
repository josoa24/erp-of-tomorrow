package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Montant inhabituel pour ce fournisseur : écart de plus de 3 écarts-types à la moyenne de ses factures
 * (ou plus du double de la moyenne si elles sont toutes égales). Il faut au moins 5 factures d'historique.
 */
@Component
@RequiredArgsConstructor
public class AmountAnomalyCheck implements InvoiceCheck {

    static final int MIN_HISTORY = 5;
    static final double Z_THRESHOLD = 3.0;

    private final SupplierInvoiceRepository invoiceRepository;

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        List<Double> history = invoiceRepository
                .findBySupplierIdAndStatusNot(context.invoice().supplierId(), InvoiceStatus.REJECTED).stream()
                .map(SupplierInvoice::getTotalAmount)
                .map(Number::doubleValue)
                .toList();
        if (history.size() < MIN_HISTORY) {
            return List.of();
        }
        double mean = history.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = history.stream().mapToDouble(value -> Math.pow(value - mean, 2)).average().orElse(0);
        double deviation = Math.sqrt(variance);
        double amount = context.invoice().totalAmount().doubleValue();
        boolean unusual = deviation == 0 ? amount > 2 * mean : (amount - mean) / deviation > Z_THRESHOLD;
        if (!unusual) {
            return List.of();
        }
        return List.of(ShieldAlert.warning(AlertCode.AMOUNT_ANOMALY, String.format(
                "Montant inhabituel pour ce fournisseur : %.0f Ar pour une moyenne de %.0f Ar sur %d factures",
                amount, mean, history.size())));
    }
}
