package com.hackaton.stock.movement.service.creator;

import com.hackaton.stock.common.exception.InsufficientStockException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.lot.service.tracker.MovementLotTracker;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.movement.mapper.StockMovementMapper;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockMovementCreatorService implements StockMovementCreator {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final StockMovementMapper movementMapper;
    private final MovementLotTracker lotTracker;

    @Override
    @Transactional
    public MovementResponse record(Long productId, MovementRequest request) {
        lotTracker.check(request);
        Product product = productRepository.findForUpdateById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", productId));
        int newQuantity = computeNewQuantity(product, request);
        product.setQuantity(newQuantity);
        productRepository.save(product);
        StockMovement movement = movementRepository.save(buildMovement(productId, request, newQuantity));
        lotTracker.track(product, movement, request);
        return movementMapper.toResponse(movement);
    }

    private int computeNewQuantity(Product product, MovementRequest request) {
        if (request.type() == MovementType.IN) {
            return product.getQuantity() + request.quantity();
        }
        if (request.quantity() > product.getQuantity()) {
            throw new InsufficientStockException(product.getName(), product.getQuantity(), request.quantity());
        }
        return product.getQuantity() - request.quantity();
    }

    private StockMovement buildMovement(Long productId, MovementRequest request, int stockAfter) {
        StockMovement movement = new StockMovement();
        movement.setProductId(productId);
        movement.setType(request.type());
        movement.setQuantity(request.quantity());
        movement.setReason(request.reason());
        movement.setStockAfter(stockAfter);
        return movement;
    }
}
