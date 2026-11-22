package com.hackaton.vente.sale.service.terms;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import org.springframework.stereotype.Component;

@Component
public class SaleTermsApplier {

    public void apply(Sale sale, SaleRequest request) {
        boolean onCredit = request.paymentMethod() == PaymentMethod.CREDIT;
        if (onCredit && request.creditDays() == null) {
            throw new BusinessException("Le délai de crédit en jours est obligatoire pour une vente à crédit");
        }
        if (onCredit && request.customerId() == null) {
            throw new BusinessException("Une vente à crédit doit être rattachée à un client");
        }
        sale.setPaymentMethod(request.paymentMethod());
        sale.setCreditDays(onCredit ? request.creditDays() : null);
        sale.setToDeliver(Boolean.TRUE.equals(request.toDeliver()));
    }
}
