package com.hackaton.vente.sale.service.facts;

import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.payment.service.loader.PaidAmountLoader;
import com.hackaton.vente.sale.entity.Sale;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleFactsLoader {

    private final CustomerRepository customerRepository;
    private final PaidAmountLoader paidAmountLoader;
    private final SaleCancellationRepository cancellationRepository;

    public SaleFacts load(List<Sale> sales) {
        if (sales.isEmpty()) {
            return SaleFacts.none();
        }
        Set<Long> saleIds = sales.stream().map(Sale::getId).collect(Collectors.toSet());
        return new SaleFacts(
                customerNames(sales),
                paidAmountLoader.bySale(saleIds),
                cancellationRepository.findCancelledSaleIds(saleIds));
    }

    private Map<Long, String> customerNames(List<Sale> sales) {
        Set<Long> customerIds = sales.stream()
                .map(Sale::getCustomerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (customerIds.isEmpty()) {
            return Map.of();
        }
        return customerRepository.findAllById(customerIds).stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getName));
    }
}
