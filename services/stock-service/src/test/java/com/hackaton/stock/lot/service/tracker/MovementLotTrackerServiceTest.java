package com.hackaton.stock.lot.service.tracker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.hackaton.stock.lot.service.allocator.StockLotAllocator;
import com.hackaton.stock.lot.service.creator.StockLotCreator;
import com.hackaton.stock.lot.service.validator.LotEntryValidator;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovementLotTrackerServiceTest {

    private static final LocalDate EXPIRY = LocalDate.of(2026, 12, 15);

    @Mock
    private LotEntryValidator entryValidator;
    @Mock
    private StockLotCreator lotCreator;
    @Mock
    private StockLotAllocator lotAllocator;
    @InjectMocks
    private MovementLotTrackerService tracker;

    private final Product product = new Product();

    private StockMovement movement(MovementType type) {
        StockMovement movement = new StockMovement();
        movement.setType(type);
        movement.setQuantity(12);
        return movement;
    }

    @Test
    void shouldDelegateLotRules_whenChecking() {
        MovementRequest request = new MovementRequest(MovementType.IN, 12, null, "LC-1", EXPIRY);

        tracker.check(request);

        verify(entryValidator).validate(request);
    }

    @Test
    void shouldCreateTrimmedLot_whenEntryCarriesLot() {
        StockMovement entry = movement(MovementType.IN);

        tracker.track(product, entry, new MovementRequest(MovementType.IN, 12, null, " LC-1 ", EXPIRY));

        verify(lotCreator).receive(product, entry, "LC-1", EXPIRY);
        verifyNoInteractions(lotAllocator);
    }

    @Test
    void shouldAllocateFefo_whenMovementIsExit() {
        StockMovement exit = movement(MovementType.OUT);

        tracker.track(product, exit, new MovementRequest(MovementType.OUT, 12, "Vente V-0412"));

        verify(lotAllocator).allocate(exit);
        verifyNoInteractions(lotCreator);
    }

    @Test
    void shouldNotCreateLot_whenEntryHasNoLot() {
        tracker.track(product, movement(MovementType.IN), new MovementRequest(MovementType.IN, 12, "Réception"));

        verify(lotCreator, never()).receive(any(), any(), any(), any());
        verifyNoInteractions(entryValidator);
    }
}
