package com.hackaton.vente.payment.service.recorder;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.mapper.SaleNumber;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class PaymentRules {

    public void check(Sale sale, BigDecimal amount, PaymentMethod method, BigDecimal alreadyPaid, boolean cancelled) {
        String number = SaleNumber.of(sale.getId());
        if (method == PaymentMethod.CREDIT) {
            throw new BusinessException("Le crédit n'est pas un moyen d'encaissement");
        }
        if (sale.getStatus() != DocumentStatus.VALIDATED) {
            throw new BusinessException("La vente " + number + " est un brouillon : validez-la avant de l'encaisser");
        }
        if (cancelled) {
            throw new BusinessException("La vente " + number + " est annulée et ne peut pas être encaissée");
        }
        BigDecimal remaining = sale.getTotalAmount().subtract(alreadyPaid);
        if (amount.compareTo(remaining) > 0) {
            throw new BusinessException("Le montant dépasse le reste à encaisser de la vente " + number + " : "
                    + remaining.max(BigDecimal.ZERO).stripTrailingZeros().toPlainString() + " Ar");
        }
    }
}
