package com.hackaton.stock.product.service.reader;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAutonomyReaderService implements ProductAutonomyReader {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final AutonomyCalculator calculator;

    @Override
    public AutonomyResponse analyze(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", productId));
        Instant since = Instant.now().minus(AutonomyCalculator.ANALYSIS_DAYS, ChronoUnit.DAYS);
        long outQuantity = movementRepository.sumQuantitySince(productId, MovementType.OUT, since);
        return calculator.calculate(product, outQuantity);
    }
}
