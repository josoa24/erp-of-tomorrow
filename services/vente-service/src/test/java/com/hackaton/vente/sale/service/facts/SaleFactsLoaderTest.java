package com.hackaton.vente.sale.service.facts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.payment.service.loader.PaidAmountLoader;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.Sale;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleFactsLoaderTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaidAmountLoader paidAmountLoader;
    @Mock
    private SaleCancellationRepository cancellationRepository;
    @InjectMocks
    private SaleFactsLoader loader;

    @Test
    void shouldLoadNamesPaymentsAndCancellations_whenSalesAreGiven() {
        Sale withCustomer = SaleFixtures.draftSale(1L, SaleFixtures.line(10L, "Riz", 1, "1000"));
        withCustomer.setCustomerId(7L);
        Sale walkIn = SaleFixtures.draftSale(2L, SaleFixtures.line(10L, "Riz", 1, "1000"));
        Customer customer = new Customer();
        customer.setId(7L);
        customer.setName("Hery Rabe");
        when(customerRepository.findAllById(Set.of(7L))).thenReturn(List.of(customer));
        when(paidAmountLoader.bySale(Set.of(1L, 2L))).thenReturn(Map.of(1L, new BigDecimal("500")));
        when(cancellationRepository.findCancelledSaleIds(Set.of(1L, 2L))).thenReturn(Set.of(2L));

        SaleFacts facts = loader.load(List.of(withCustomer, walkIn));

        assertEquals("Hery Rabe", facts.customerName(withCustomer));
        assertEquals("Client comptoir", facts.customerName(walkIn));
        assertEquals(new BigDecimal("500"), facts.paidAmount(withCustomer));
        assertEquals(BigDecimal.ZERO, facts.paidAmount(walkIn));
        assertFalse(facts.isCancelled(withCustomer));
        assertTrue(facts.isCancelled(walkIn));
    }

    @Test
    void shouldSkipCustomerLookup_whenNoSaleHasCustomer() {
        Sale walkIn = SaleFixtures.draftSale(2L, SaleFixtures.line(10L, "Riz", 1, "1000"));
        when(paidAmountLoader.bySale(Set.of(2L))).thenReturn(Map.of());
        when(cancellationRepository.findCancelledSaleIds(Set.of(2L))).thenReturn(Set.of());

        loader.load(List.of(walkIn));

        verify(customerRepository, never()).findAllById(any());
    }

    @Test
    void shouldReturnEmptyFacts_whenThereAreNoSales() {
        SaleFacts facts = loader.load(List.of());

        assertEquals(SaleFacts.none(), facts);
        verifyNoInteractions(customerRepository, paidAmountLoader, cancellationRepository);
    }
}
