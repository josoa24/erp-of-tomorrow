package com.hackaton.vente.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.vente.catalog.dto.CatalogProductResponse;
import com.hackaton.vente.common.exception.ServiceUnavailableException;
import com.hackaton.vente.stock.client.StockCatalogProduct;
import com.hackaton.vente.stock.client.StockClient;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogReaderServiceTest {

    @Mock
    private StockClient stockClient;
    @InjectMocks
    private CatalogReaderService service;

    @Test
    void shouldExposeStockProductsForSale_whenStockAnswers() {
        when(stockClient.searchProducts("riz")).thenReturn(List.of(
                new StockCatalogProduct(10L, "Riz blanc 50 kg", "RIZ-50", "sac", new BigDecimal("93000"), 40)));

        List<CatalogProductResponse> products = service.search("riz");

        assertEquals(List.of(new CatalogProductResponse(
                10L, "Riz blanc 50 kg", "RIZ-50", "sac", new BigDecimal("93000"), 40)), products);
    }

    @Test
    void shouldPropagateUnavailability_whenStockIsDown() {
        when(stockClient.searchProducts(null))
                .thenThrow(new ServiceUnavailableException("Le service Stock est indisponible"));

        assertThrows(ServiceUnavailableException.class, () -> service.search(null));
    }
}
