package com.hackaton.vente.kpi.service.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import com.hackaton.vente.kpi.dto.CreditSummaryResponse;
import com.hackaton.vente.kpi.dto.CustomerCreditResponse;
import com.hackaton.vente.payment.service.loader.PaidAmountLoader;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.status.PaymentStatusResolver;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreditReaderServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private PaidAmountLoader paidAmountLoader;

    private CreditReaderService service;

    @BeforeEach
    void setUp() {
        service = new CreditReaderService(saleRepository, paidAmountLoader,
                new PaymentStatusResolver(SaleTestAssembler.clockAt(SaleTestAssembler.NOW)));
        when(saleRepository.findEffectiveByPaymentMethod(PaymentMethod.CREDIT)).thenReturn(List.of(
                credit(1L, 7L, "2026-11-01T08:00:00Z", 15, "760000"),
                credit(2L, 7L, "2026-11-20T08:00:00Z", 30, "420000"),
                credit(3L, 8L, "2026-11-22T08:00:00Z", 30, "300000"),
                credit(4L, 9L, "2026-10-01T08:00:00Z", 30, "100000")));
        when(paidAmountLoader.bySale(anyCollection())).thenReturn(Map.of(
                1L, new BigDecimal("160000"), 4L, new BigDecimal("100000")));
    }

    private Sale credit(Long id, Long customerId, String validatedAt, int days, String amount) {
        Sale sale = SaleFixtures.validatedSale(id, validatedAt, PaymentMethod.CREDIT,
                SaleFixtures.line(10L, "Riz", 1, amount));
        sale.setCustomerId(customerId);
        sale.setCreditDays(days);
        return sale;
    }

    @Test
    void shouldGroupOutstandingByCustomerAndSkipSettledSales() {
        List<CustomerCreditResponse> rows = service.byCustomer();

        assertEquals(List.of(
                new CustomerCreditResponse(7L, new BigDecimal("1020000"), 1),
                new CustomerCreditResponse(8L, new BigDecimal("300000"), 0)), rows);
    }

    @Test
    void shouldSummarizeOutstandingCustomersAndOverdueCustomers() {
        CreditSummaryResponse summary = service.summarize();

        assertEquals(new CreditSummaryResponse(new BigDecimal("1320000"), 2, 1), summary);
    }
}
