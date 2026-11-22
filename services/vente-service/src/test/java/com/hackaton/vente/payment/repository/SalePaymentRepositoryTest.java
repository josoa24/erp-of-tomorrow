package com.hackaton.vente.payment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.vente.cancellation.entity.SaleCancellation;
import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.payment.entity.SalePayment;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class SalePaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private SalePaymentRepository paymentRepository;
    @Autowired
    private SaleCancellationRepository cancellationRepository;

    private Sale paidSale;
    private Sale unpaidSale;

    private Sale persistSale() {
        Sale sale = SaleFixtures.draftSale(null, SaleFixtures.line(10L, "Riz", 1, "760000"));
        sale.setStatus(DocumentStatus.VALIDATED);
        sale.setValidatedAt(Instant.parse("2026-11-20T08:00:00Z"));
        return entityManager.persist(sale);
    }

    private void pay(Sale sale, String amount, String paidAt) {
        entityManager.persist(new SalePayment(sale, new BigDecimal(amount), PaymentMethod.MVOLA,
                Instant.parse(paidAt), null, 1L));
    }

    @BeforeEach
    void setUp() {
        paidSale = persistSale();
        unpaidSale = persistSale();
        pay(paidSale, "300000", "2026-11-23T20:00:00Z");
        pay(paidSale, "160000", "2026-11-24T08:00:00Z");
        entityManager.persist(new SaleCancellation(unpaidSale, "Retour", 1L, Instant.parse("2026-11-24T09:00:00Z")));
        entityManager.flush();
    }

    @Test
    void shouldSumPaymentsPerSale() {
        Map<Long, BigDecimal> totals = paymentRepository
                .sumAmountBySaleIds(List.of(paidSale.getId(), unpaidSale.getId()))
                .stream().collect(Collectors.toMap(SalePaidTotal::getSaleId, SalePaidTotal::getTotal));

        assertEquals(1, totals.size());
        assertEquals(0, new BigDecimal("460000").compareTo(totals.get(paidSale.getId())));
    }

    @Test
    void shouldReturnZero_whenSaleHasNoPayment() {
        assertEquals(0, BigDecimal.ZERO.compareTo(paymentRepository.sumAmountBySaleId(unpaidSale.getId())));
    }

    @Test
    void shouldSumPaymentsWithinInstantRange() {
        BigDecimal collected = paymentRepository.sumAmountPaidBetween(
                Instant.parse("2026-11-23T21:00:00Z"), Instant.parse("2026-11-24T21:00:00Z"));

        assertEquals(0, new BigDecimal("160000").compareTo(collected));
    }

    @Test
    void shouldListPaymentsInPaymentOrder() {
        List<SalePayment> payments = paymentRepository.findBySale_IdOrderByPaidAtAscIdAsc(paidSale.getId());

        assertEquals(List.of("300000", "160000"),
                payments.stream().map(payment -> payment.getAmount().stripTrailingZeros().toPlainString()).toList());
    }

    @Test
    void shouldFindCancelledSaleIdsAmongGivenSales() {
        assertEquals(Set.of(unpaidSale.getId()),
                cancellationRepository.findCancelledSaleIds(List.of(paidSale.getId(), unpaidSale.getId())));
    }
}
