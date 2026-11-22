package com.hackaton.stock.lot.service.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.entity.StockLot;
import com.hackaton.stock.lot.entity.StockLotAllocation;
import com.hackaton.stock.lot.repository.StockLotAllocationRepository;
import com.hackaton.stock.lot.repository.StockLotRepository;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockLotAllocatorServiceTest {

    private static final LocalDate EXPIRY = LocalDate.of(2026, 12, 15);
    private static final Instant RECEIVED = Instant.parse("2026-11-02T07:00:00Z");

    @Mock
    private StockLotRepository lotRepository;
    @Mock
    private StockLotAllocationRepository allocationRepository;

    private StockLotAllocatorService service;
    private StockMovement exit;

    @BeforeEach
    void setUp() {
        service = new StockLotAllocatorService(lotRepository, allocationRepository, new FefoPlanner());
        exit = new StockMovement();
        exit.setProductId(3L);
        exit.setType(MovementType.OUT);
        exit.setQuantity(9);
    }

    @Test
    void shouldSaveOneAllocationPerLotTaken_whenExitSpansTwoLots() {
        StockLot first = mock(StockLot.class);
        StockLot second = mock(StockLot.class);
        when(lotRepository.findBalancesByProductId(3L)).thenReturn(List.of(
                new LotBalance(1L, 3L, "LC-1", EXPIRY, 10, 6, RECEIVED),
                new LotBalance(2L, 3L, "LC-2", EXPIRY.plusDays(20), 20, 0, RECEIVED)));
        when(lotRepository.getReferenceById(1L)).thenReturn(first);
        when(lotRepository.getReferenceById(2L)).thenReturn(second);

        service.allocate(exit);

        ArgumentCaptor<StockLotAllocation> captor = ArgumentCaptor.forClass(StockLotAllocation.class);
        verify(allocationRepository, times(2)).save(captor.capture());
        List<StockLotAllocation> saved = captor.getAllValues();
        assertSame(first, saved.get(0).getLot());
        assertEquals(4, saved.get(0).getQuantity());
        assertSame(second, saved.get(1).getLot());
        assertEquals(5, saved.get(1).getQuantity());
        assertSame(exit, saved.get(1).getMovement());
    }

    @Test
    void shouldSaveNothing_whenProductHasNoOpenLot() {
        when(lotRepository.findBalancesByProductId(3L)).thenReturn(List.of());

        service.allocate(exit);

        verify(allocationRepository, never()).save(any());
    }
}
