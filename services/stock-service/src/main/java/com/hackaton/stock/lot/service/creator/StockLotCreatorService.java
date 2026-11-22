package com.hackaton.stock.lot.service.creator;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.lot.entity.StockLot;
import com.hackaton.stock.lot.repository.StockLotRepository;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockLotCreatorService implements StockLotCreator {

    private final StockLotRepository lotRepository;
    private final Clock clock;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void receive(Product product, StockMovement movement, String lotNumber, LocalDate expiryDate) {
        if (lotRepository.existsByProductIdAndLotNumber(product.getId(), lotNumber)) {
            throw new DuplicateResourceException(
                    "Le lot " + lotNumber + " existe déjà pour " + product.getName());
        }
        lotRepository.save(new StockLot(product, movement, lotNumber, expiryDate, Instant.now(clock)));
    }
}
