package com.hackaton.stock.lot.service.tracker;

import com.hackaton.stock.lot.service.allocator.StockLotAllocator;
import com.hackaton.stock.lot.service.creator.StockLotCreator;
import com.hackaton.stock.lot.service.validator.LotEntryValidator;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MovementLotTrackerService implements MovementLotTracker {

    private final LotEntryValidator entryValidator;
    private final StockLotCreator lotCreator;
    private final StockLotAllocator lotAllocator;

    @Override
    public void check(MovementRequest request) {
        entryValidator.validate(request);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void track(Product product, StockMovement movement, MovementRequest request) {
        if (movement.getType() == MovementType.OUT) {
            lotAllocator.allocate(movement);
            return;
        }
        if (StringUtils.hasText(request.lotNumber())) {
            lotCreator.receive(product, movement, request.lotNumber().trim(), request.expiryDate());
        }
    }
}
