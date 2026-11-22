package com.hackaton.vente.sale.service.updater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.SaleLineFactory;
import com.hackaton.vente.sale.service.deleter.SaleDeleterService;
import com.hackaton.vente.sale.service.terms.SaleTermsApplier;
import com.hackaton.vente.stock.client.StockClient;
import com.hackaton.vente.stock.client.StockProduct;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleUpdaterAndDeleterTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private StockClient stockClient;

    private SaleUpdaterService updater;
    private SaleDeleterService deleter;
    private Sale sale;

    @BeforeEach
    void setUp() {
        updater = new SaleUpdaterService(saleRepository, customerRepository, new SaleLineFactory(stockClient),
                new SaleTermsApplier(), SaleTestAssembler.withoutFacts());
        deleter = new SaleDeleterService(saleRepository);
        sale = SaleFixtures.draftSale(1L, SaleFixtures.line(10L, "Riz", 1, "45000"));
    }

    @Test
    void shouldReplaceLines_whenSaleIsDraft() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(stockClient.getProduct(20L)).thenReturn(new StockProduct(20L, "Lait", new BigDecimal("3000"), 10));
        when(saleRepository.save(any(Sale.class))).thenAnswer(call -> call.getArgument(0));

        SaleResponse response = updater.update(1L, SaleFixtures.request(null, 20L, 2));

        assertEquals(1, response.lines().size());
        assertEquals(new BigDecimal("6000"), response.totalAmount());
    }

    @Test
    void shouldRefuseUpdate_whenSaleIsValidated() {
        sale.setStatus(DocumentStatus.VALIDATED);
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThrows(ImmutableResourceException.class,
                () -> updater.update(1L, SaleFixtures.request(null, 20L, 1)));
        verify(saleRepository, never()).save(any());
    }

    @Test
    void shouldRefuseDelete_whenSaleIsValidated() {
        sale.setStatus(DocumentStatus.VALIDATED);
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThrows(ImmutableResourceException.class, () -> deleter.delete(1L));
        verify(saleRepository, never()).delete(any());
    }

    @Test
    void shouldDelete_whenSaleIsDraft() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        deleter.delete(1L);

        verify(saleRepository).delete(sale);
    }
}
