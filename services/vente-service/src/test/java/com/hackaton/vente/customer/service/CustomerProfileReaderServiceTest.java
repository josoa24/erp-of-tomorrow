package com.hackaton.vente.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.customer.dto.CustomerProfileResponse;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.customer.service.profile.CustomerProfileReaderService;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerProfileReaderServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private SaleRepository saleRepository;

    private CustomerProfileReaderService service;
    private Customer customer;

    private Sale validated(Long id, String validatedAt, SaleLine... lines) {
        Sale sale = SaleFixtures.draftSale(id, lines);
        sale.setStatus(DocumentStatus.VALIDATED);
        sale.setValidatedAt(Instant.parse(validatedAt));
        return sale;
    }

    @BeforeEach
    void setUp() {
        service = new CustomerProfileReaderService(customerRepository, saleRepository, new CustomerMapper());
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Epicerie Rakoto");
    }

    @Test
    void shouldComputeTotalsAverageLastPurchaseAndTopProducts() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(saleRepository.findByCustomerIdAndStatusOrderByValidatedAtDesc(1L, DocumentStatus.VALIDATED))
                .thenReturn(List.of(
                        validated(2L, "2026-11-20T10:00:00Z",
                                SaleFixtures.line(10L, "Riz", 5, "1000"), SaleFixtures.line(20L, "Lait", 30, "100")),
                        validated(1L, "2026-11-10T10:00:00Z", SaleFixtures.line(10L, "Riz", 10, "1000"))));

        CustomerProfileResponse profile = service.findProfile(1L);

        assertEquals(2, profile.salesCount());
        assertEquals(new BigDecimal("18000"), profile.totalPurchased());
        assertEquals(new BigDecimal("9000.00"), profile.averageBasket());
        assertEquals(Instant.parse("2026-11-20T10:00:00Z"), profile.lastPurchaseAt());
        assertEquals("Lait", profile.topProducts().get(0).productName());
        assertEquals(30, profile.topProducts().get(0).totalQuantity());
        assertEquals(15, profile.topProducts().get(1).totalQuantity());
        assertEquals(2L, profile.recentSales().get(0).id());
    }

    @Test
    void shouldReturnZerosAndNoLastPurchase_whenCustomerNeverBought() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(saleRepository.findByCustomerIdAndStatusOrderByValidatedAtDesc(1L, DocumentStatus.VALIDATED))
                .thenReturn(List.of());

        CustomerProfileResponse profile = service.findProfile(1L);

        assertEquals(0, profile.salesCount());
        assertEquals(BigDecimal.ZERO, profile.totalPurchased());
        assertEquals(BigDecimal.ZERO, profile.averageBasket());
        assertNull(profile.lastPurchaseAt());
        assertEquals(0, profile.topProducts().size());
    }

    @Test
    void shouldKeepOnlyTheFiveMostRecentSales() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        List<Sale> sales = new ArrayList<>();
        for (long i = 8; i >= 1; i--) {
            sales.add(validated(i, "2026-11-0" + i + "T10:00:00Z", SaleFixtures.line(10L, "Riz", 1, "1000")));
        }
        when(saleRepository.findByCustomerIdAndStatusOrderByValidatedAtDesc(1L, DocumentStatus.VALIDATED))
                .thenReturn(sales);

        CustomerProfileResponse profile = service.findProfile(1L);

        assertEquals(8, profile.salesCount());
        assertEquals(5, profile.recentSales().size());
        assertEquals(8L, profile.recentSales().get(0).id());
    }

    @Test
    void shouldThrowNotFound_whenCustomerDoesNotExist() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findProfile(9L));
    }
}
