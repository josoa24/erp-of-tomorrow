package com.hackaton.vente.sale.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.dto.SaleLineRequest;
import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.SaleLineFactory;
import com.hackaton.vente.sale.service.terms.SaleTermsApplier;
import com.hackaton.vente.stock.client.StockClient;
import com.hackaton.vente.stock.client.StockProduct;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleCreatorServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private StockClient stockClient;

    private SaleCreatorService service;

    @BeforeEach
    void setUp() {
        service = new SaleCreatorService(saleRepository, customerRepository, new SaleLineFactory(stockClient),
                new SaleTermsApplier(), SaleTestAssembler.withoutFacts());
    }

    @Test
    void shouldCopyNameAndPriceFromStockAndComputeTotal() {
        when(stockClient.getProduct(10L)).thenReturn(new StockProduct(10L, "Riz blanc", new BigDecimal("45000"), 59));
        when(saleRepository.save(any(Sale.class))).thenAnswer(call -> SaleFixtures.withId(call.getArgument(0), 41L));

        SaleResponse response = service.create(SaleFixtures.request(null, 10L, 2));

        assertEquals("V-0041", response.number());
        assertEquals(DocumentStatus.DRAFT, response.status());
        assertEquals("Riz blanc", response.lines().get(0).productName());
        assertEquals(new BigDecimal("90000"), response.totalAmount());
        assertEquals("Client comptoir", response.customerName());
    }

    @Test
    void shouldStoreCreditTerms_whenSaleIsOnCredit() {
        when(customerRepository.existsById(7L)).thenReturn(true);
        when(stockClient.getProduct(10L)).thenReturn(new StockProduct(10L, "Riz blanc", new BigDecimal("45000"), 59));
        when(saleRepository.save(any(Sale.class))).thenAnswer(call -> SaleFixtures.withId(call.getArgument(0), 42L));

        SaleResponse response = service.create(new SaleRequest(
                7L, List.of(new SaleLineRequest(10L, 1)), PaymentMethod.CREDIT, 30, true));

        assertEquals(PaymentMethod.CREDIT, response.paymentMethod());
        assertEquals(30, response.creditDays());
        assertTrue(response.toDeliver());
    }

    @Test
    void shouldThrowNotFound_whenCustomerDoesNotExist() {
        when(customerRepository.existsById(5L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.create(SaleFixtures.request(5L, 10L, 1)));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void shouldNotSave_whenStockRefusesTheProduct() {
        when(stockClient.getProduct(99L)).thenThrow(new BusinessException("Produit introuvable avec l'id 99"));

        assertThrows(BusinessException.class,
                () -> service.create(SaleFixtures.request(null, 99L, 1)));
        verify(saleRepository, never()).save(any());
    }
}
