package com.hackaton.stock.lot.service.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.dto.LotTake;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class FefoPlannerTest {

    private static final LocalDate DECEMBER = LocalDate.of(2026, 12, 15);
    private static final Instant RECEIVED = Instant.parse("2026-11-02T07:00:00Z");

    private final FefoPlanner planner = new FefoPlanner();

    private LotBalance lot(long id, LocalDate expiry, int quantity, long allocated) {
        return new LotBalance(id, 3L, "LC-" + id, expiry, quantity, allocated, RECEIVED.plusSeconds(id));
    }

    @Test
    void shouldTakeEarliestExpiringLotFirst_whenItCoversTheExit() {
        List<LotTake> takes = planner.plan(List.of(lot(1L, DECEMBER.plusDays(30), 20, 0),
                lot(2L, DECEMBER, 20, 0)), 5);

        assertEquals(List.of(new LotTake(2L, 5)), takes);
    }

    @Test
    void shouldSpreadExitAcrossLots_whenFirstLotIsInsufficient() {
        List<LotTake> takes = planner.plan(List.of(lot(1L, DECEMBER, 10, 6), lot(2L, DECEMBER.plusDays(10), 20, 0)), 9);

        assertEquals(List.of(new LotTake(1L, 4), new LotTake(2L, 5)), takes);
    }

    @Test
    void shouldSkipExhaustedLots() {
        List<LotTake> takes = planner.plan(List.of(lot(1L, DECEMBER, 10, 10), lot(2L, DECEMBER.plusDays(1), 8, 0)), 3);

        assertEquals(List.of(new LotTake(2L, 3)), takes);
    }

    @Test
    void shouldLeaveRestUntracked_whenLotsDoNotCoverTheExit() {
        List<LotTake> takes = planner.plan(List.of(lot(1L, DECEMBER, 4, 0)), 10);

        assertEquals(List.of(new LotTake(1L, 4)), takes);
    }

    @Test
    void shouldTakeOldestReception_whenExpiryDatesAreEqual() {
        LotBalance later = new LotBalance(1L, 3L, "LC-B", DECEMBER, 5, 0, RECEIVED.plusSeconds(3600));
        LotBalance earlier = new LotBalance(2L, 3L, "LC-A", DECEMBER, 5, 0, RECEIVED);

        assertEquals(List.of(new LotTake(2L, 5), new LotTake(1L, 1)), planner.plan(List.of(later, earlier), 6));
    }

    @Test
    void shouldPlanNothing_whenProductHasNoLot() {
        assertTrue(planner.plan(List.of(), 7).isEmpty());
    }
}
