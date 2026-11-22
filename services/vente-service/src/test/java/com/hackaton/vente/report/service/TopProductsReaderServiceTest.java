package com.hackaton.vente.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.vente.report.dto.TopProductResponse;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TopProductsReaderServiceTest {

    @Mock
    private SaleRepository saleRepository;

    private TopProductsReaderService service;

    @BeforeEach
    void setUp() {
        service = new TopProductsReaderService(saleRepository);
        when(saleRepository.findEffective()).thenReturn(List.of(ReportSales.monday(), ReportSales.tuesday()));
    }

    @Test
    void shouldRankProductsByQuantityAndApplyLimit() {
        List<TopProductResponse> top = service.findTop(1);

        assertEquals(1, top.size());
        assertEquals("Lait", top.get(0).productName());
        assertEquals(20, top.get(0).totalQuantity());
    }

    @Test
    void shouldAggregateSameProductAcrossSales() {
        List<TopProductResponse> top = service.findTop(5);

        TopProductResponse rice = top.stream().filter(p -> p.productId() == 10L).findFirst().orElseThrow();
        assertEquals(15, rice.totalQuantity());
        assertEquals(new BigDecimal("15000"), rice.totalRevenue());
    }
}
