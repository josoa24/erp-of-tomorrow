package com.hackaton.stock.movement.service.reader;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.mapper.StockMovementMapper;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockMovementReaderService implements StockMovementReader {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final StockMovementMapper movementMapper;

    @Override
    public List<MovementResponse> findByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produit", productId);
        }
        return movementRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(movementMapper::toResponse)
                .toList();
    }
}
