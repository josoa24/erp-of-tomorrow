package com.hackaton.stock.lot.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.repository.StockLotRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenLotReaderServiceTest {

    private static final Instant RECEIVED = Instant.parse("2026-11-02T07:00:00Z");

    @Mock
    private StockLotRepository lotRepository;
    @InjectMocks
    private OpenLotReaderService service;

    private LotBalance lot(long id, long productId, LocalDate expiry) {
        return new LotBalance(id, productId, "L-" + id, expiry, 10, 0, RECEIVED);
    }

    @Test
    void shouldKeepFirstLotOfEachProduct_whenBalancesComeInFefoOrder() {
        LotBalance milkEarly = lot(1L, 3L, LocalDate.of(2026, 12, 1));
        LotBalance riceOnly = lot(2L, 5L, LocalDate.of(2026, 12, 10));
        LotBalance milkLate = lot(3L, 3L, LocalDate.of(2027, 1, 5));
        when(lotRepository.findOpenBalances()).thenReturn(List.of(milkEarly, riceOnly, milkLate));

        Map<Long, LotBalance> earliest = service.findEarliestOpenLotByProduct();

        assertEquals(Map.of(3L, milkEarly, 5L, riceOnly), earliest);
    }
}
