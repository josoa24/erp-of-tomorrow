package com.hackaton.vente.sale.service.reader;

import com.hackaton.vente.common.time.LocalDays;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.Sale;
import java.time.Clock;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleSearchFilter {

    private final Clock clock;

    public boolean matchesStored(Sale sale, SaleSearchCriteria criteria) {
        return (criteria.customerId() == null || Objects.equals(sale.getCustomerId(), criteria.customerId()))
                && (criteria.status() == null || sale.getStatus() == criteria.status())
                && (criteria.paymentMethod() == null || sale.getPaymentMethod() == criteria.paymentMethod())
                && (criteria.toDeliver() == null || sale.isToDeliver() == criteria.toDeliver())
                && LocalDays.isWithin(sale.getCreatedAt(), criteria.from(), criteria.to(), clock.getZone());
    }

    public boolean matchesDerived(SaleResponse sale, SaleSearchCriteria criteria) {
        boolean statusMatches = criteria.paymentStatuses() == null || criteria.paymentStatuses().isEmpty()
                || criteria.paymentStatuses().contains(sale.paymentStatus());
        String term = SearchText.normalize(criteria.search());
        boolean textMatches =
                SearchText.contains(sale.number(), term) || SearchText.contains(sale.customerName(), term);
        return statusMatches && textMatches;
    }
}
