package com.hackaton.stock.lot.service.allocator;

import com.hackaton.stock.lot.dto.LotTake;
import com.hackaton.stock.lot.entity.StockLotAllocation;
import com.hackaton.stock.lot.repository.StockLotAllocationRepository;
import com.hackaton.stock.lot.repository.StockLotRepository;
import com.hackaton.stock.movement.entity.StockMovement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockLotAllocatorService implements StockLotAllocator {

    private final StockLotRepository lotRepository;
    private final StockLotAllocationRepository allocationRepository;
    private final FefoPlanner planner;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void allocate(StockMovement exit) {
        List<LotTake> takes = planner.plan(
                lotRepository.findBalancesByProductId(exit.getProductId()), exit.getQuantity());
        for (LotTake take : takes) {
            allocationRepository.save(new StockLotAllocation(
                    lotRepository.getReferenceById(take.lotId()), exit, take.quantity()));
        }
    }
}
