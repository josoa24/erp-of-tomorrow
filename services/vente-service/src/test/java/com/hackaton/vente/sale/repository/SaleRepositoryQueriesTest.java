package com.hackaton.vente.sale.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.vente.cancellation.entity.SaleCancellation;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class SaleRepositoryQueriesTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private SaleRepository saleRepository;

    private Sale cashSale;
    private Sale creditSale;

    private Sale persist(DocumentStatus status, PaymentMethod method, String validatedAt) {
        Sale sale = SaleFixtures.draftSale(null, SaleFixtures.line(10L, "Riz", 1, "1000"));
        sale.setStatus(status);
        sale.setPaymentMethod(method);
        sale.setValidatedAt(validatedAt == null ? null : Instant.parse(validatedAt));
        return entityManager.persist(sale);
    }

    @BeforeEach
    void setUp() {
        cashSale = persist(DocumentStatus.VALIDATED, PaymentMethod.CASH, "2026-10-20T08:00:00Z");
        creditSale = persist(DocumentStatus.VALIDATED, PaymentMethod.CREDIT, "2026-11-20T08:00:00Z");
        Sale cancelled = persist(DocumentStatus.VALIDATED, PaymentMethod.CREDIT, "2026-11-21T08:00:00Z");
        persist(DocumentStatus.DRAFT, PaymentMethod.CREDIT, null);
        entityManager.persist(new SaleCancellation(cancelled, "Erreur", 1L, Instant.parse("2026-11-21T09:00:00Z")));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldReturnValidatedUncancelledSales_whenLookingForEffectiveSales() {
        List<Long> ids = saleRepository.findEffective().stream().map(Sale::getId).toList();

        assertEquals(2, ids.size());
        assertTrue(ids.containsAll(List.of(cashSale.getId(), creditSale.getId())));
    }

    @Test
    void shouldFilterEffectiveSalesByValidationInstant() {
        List<Sale> sales = saleRepository.findEffectiveValidatedSince(Instant.parse("2026-11-01T00:00:00Z"));

        assertEquals(List.of(creditSale.getId()), sales.stream().map(Sale::getId).toList());
    }

    @Test
    void shouldFilterEffectiveSalesByPaymentMethod() {
        List<Sale> sales = saleRepository.findEffectiveByPaymentMethod(PaymentMethod.CREDIT);

        assertEquals(List.of(creditSale.getId()), sales.stream().map(Sale::getId).toList());
    }

    @Test
    void shouldLoadSaleWithLines_whenLockingById() {
        Sale locked = saleRepository.findWithLockById(cashSale.getId()).orElseThrow();

        assertEquals(1, locked.getLines().size());
        assertFalse(locked.isToDeliver());
    }
}
