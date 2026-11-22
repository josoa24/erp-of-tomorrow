package com.hackaton.stock.lot.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.dto.LotResponse;
import com.hackaton.stock.lot.mapper.StockLotMapper;
import com.hackaton.stock.lot.repository.StockLotRepository;
import com.hackaton.stock.product.repository.ProductRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockLotReaderServiceTest {

    private static final Instant RECEIVED = Instant.parse("2026-11-02T07:00:00Z");

    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockLotRepository lotRepository;

    private StockLotReaderService service;

    @BeforeEach
    void setUp() {
        service = new StockLotReaderService(productRepository, lotRepository, new StockLotMapper());
    }

    @Test
    void shouldReturnLotsWithRemainingQuantity_whenProductExists() {
        when(productRepository.existsById(3L)).thenReturn(true);
        when(lotRepository.findBalancesByProductId(3L)).thenReturn(List.of(
                new LotBalance(1L, 3L, "LC-2611", LocalDate.of(2026, 12, 15), 48, 30, RECEIVED)));

        List<LotResponse> lots = service.findByProductId(3L);

        assertEquals(List.of(new LotResponse("LC-2611", LocalDate.of(2026, 12, 15), 48, 18, RECEIVED)), lots);
    }

    @Test
    void shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findByProductId(9L));
        verifyNoInteractions(lotRepository);
    }
}
