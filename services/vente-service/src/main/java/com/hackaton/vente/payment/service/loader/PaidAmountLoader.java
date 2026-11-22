package com.hackaton.vente.payment.service.loader;

import com.hackaton.vente.payment.repository.SalePaidTotal;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaidAmountLoader {

    private final SalePaymentRepository paymentRepository;

    public Map<Long, BigDecimal> bySale(Collection<Long> saleIds) {
        if (saleIds.isEmpty()) {
            return Map.of();
        }
        return paymentRepository.sumAmountBySaleIds(saleIds).stream()
                .collect(Collectors.toMap(SalePaidTotal::getSaleId, SalePaidTotal::getTotal));
    }
}
