package com.hackaton.stock.summary.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.summary.dto.StockSummaryResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockSummaryReaderServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private StockSummaryReaderService service;

    @Test
    void shouldAggregateCountsAndStockValue() {
        when(productRepository.count()).thenReturn(12L);
        when(productRepository.countLowStock()).thenReturn(3L);
        when(productRepository.totalStockValue()).thenReturn(new BigDecimal("855000.00"));

        StockSummaryResponse summary = service.summarize();

        assertEquals(12L, summary.totalProducts());
        assertEquals(3L, summary.lowStockProducts());
        assertEquals(new BigDecimal("855000.00"), summary.totalStockValue());
    }
}
