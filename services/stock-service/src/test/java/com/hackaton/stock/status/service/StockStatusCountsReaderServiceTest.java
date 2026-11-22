package com.hackaton.stock.status.service;

import static com.hackaton.stock.status.StatusItemFixtures.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.status.dto.StockStatusCounts;
import com.hackaton.stock.status.dto.StockStatusQuery;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockStatusCountsReaderServiceTest {

    @Mock
    private StockStatusReader statusReader;
    @InjectMocks
    private StockStatusCountsReaderService service;

    @Test
    void shouldCountEachFilter_whenProductsHaveMixedStatuses() {
        when(statusReader.findAll(StockStatusQuery.everything())).thenReturn(List.of(
                item(1L, "Huile 5 L", "Épicerie", StockRisk.CRITICAL, true, 23),
                item(2L, "Sucre", null, StockRisk.WARNING, true, null),
                item(3L, "Savon", null, StockRisk.CRITICAL, false, 45),
                item(4L, "Lait", null, StockRisk.OK, false, 12),
                item(5L, "Bougies", null, StockRisk.OK, false, null)));

        StockStatusCounts counts = service.count(null);

        assertEquals(new StockStatusCounts(5, 2, 2, 2), counts);
    }

    @Test
    void shouldCountOnlySearchedProducts_whenSearchIsGiven() {
        when(statusReader.findAll(StockStatusQuery.searching("huile"))).thenReturn(List.of(
                item(1L, "Huile 5 L", "Épicerie", StockRisk.CRITICAL, true, 23)));

        assertEquals(new StockStatusCounts(1, 1, 1, 1), service.count("huile"));
    }

    @Test
    void shouldReturnZeros_whenThereAreNoProducts() {
        when(statusReader.findAll(StockStatusQuery.everything())).thenReturn(List.of());

        assertEquals(new StockStatusCounts(0, 0, 0, 0), service.count(null));
    }
}
