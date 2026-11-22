package com.hackaton.vente.kpi.service.credit;

import com.hackaton.vente.kpi.dto.CreditSummaryResponse;
import com.hackaton.vente.kpi.dto.CustomerCreditResponse;
import com.hackaton.vente.payment.service.loader.PaidAmountLoader;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.status.PaymentStatusResolver;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditReaderService implements CreditReader {

    private final SaleRepository saleRepository;
    private final PaidAmountLoader paidAmountLoader;
    private final PaymentStatusResolver statusResolver;

    @Override
    public CreditSummaryResponse summarize() {
        List<CustomerCreditResponse> customers = byCustomer();
        BigDecimal outstanding = customers.stream()
                .map(CustomerCreditResponse::outstanding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long overdueCustomers = customers.stream().filter(customer -> customer.overdueCount() > 0).count();
        return new CreditSummaryResponse(outstanding, customers.size(), overdueCustomers);
    }

    @Override
    public List<CustomerCreditResponse> byCustomer() {
        List<Sale> creditSales = saleRepository.findEffectiveByPaymentMethod(PaymentMethod.CREDIT);
        Map<Long, BigDecimal> paidBySale = paidAmountLoader.bySale(creditSales.stream().map(Sale::getId).toList());
        Map<Long, CustomerCreditResponse> byCustomer = new LinkedHashMap<>();
        for (Sale sale : creditSales) {
            BigDecimal paid = paidBySale.getOrDefault(sale.getId(), BigDecimal.ZERO);
            BigDecimal outstanding = sale.getTotalAmount().subtract(paid);
            if (outstanding.signum() > 0) {
                long overdue = statusResolver.resolve(sale, paid, false) == PaymentStatus.OVERDUE ? 1 : 0;
                byCustomer.merge(sale.getCustomerId(),
                        new CustomerCreditResponse(sale.getCustomerId(), outstanding, overdue), this::combine);
            }
        }
        return byCustomer.values().stream()
                .sorted(Comparator.comparing(CustomerCreditResponse::outstanding).reversed())
                .toList();
    }

    private CustomerCreditResponse combine(CustomerCreditResponse current, CustomerCreditResponse added) {
        return new CustomerCreditResponse(current.customerId(), current.outstanding().add(added.outstanding()),
                current.overdueCount() + added.overdueCount());
    }
}
