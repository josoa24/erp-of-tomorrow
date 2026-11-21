package com.hackaton.vente.customer.service.profile;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.customer.dto.CustomerProfileResponse;
import com.hackaton.vente.customer.dto.RecentSale;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.report.service.ProductSalesAggregation;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerProfileReaderService implements CustomerProfileReader {

    private static final int TOP_PRODUCTS = 5;
    private static final int RECENT_SALES = 5;

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerProfileResponse findProfile(Long customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", customerId));
        List<Sale> sales = saleRepository
                .findByCustomerIdAndStatusOrderByValidatedAtDesc(customerId, DocumentStatus.VALIDATED);
        BigDecimal total = sales.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = sales.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP);
        return new CustomerProfileResponse(
                customerMapper.toResponse(customer),
                sales.size(),
                total,
                average,
                sales.isEmpty() ? null : sales.get(0).getValidatedAt(),
                ProductSalesAggregation.top(sales, TOP_PRODUCTS),
                sales.stream().limit(RECENT_SALES)
                        .map(sale -> new RecentSale(sale.getId(), sale.getTotalAmount(), sale.getValidatedAt()))
                        .toList());
    }
}
