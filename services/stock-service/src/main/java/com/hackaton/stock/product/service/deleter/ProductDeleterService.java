package com.hackaton.stock.product.service.deleter;

import com.hackaton.stock.common.exception.BusinessException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductDeleterService implements ProductDeleter {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final PurchaseOrderRepository orderRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit", id);
        }
        if (movementRepository.existsByProductId(id)) {
            throw new BusinessException("Ce produit a des mouvements de stock et ne peut pas être supprimé");
        }
        if (orderRepository.existsByLinesProductId(id)) {
            throw new BusinessException("Ce produit figure dans un bon de commande et ne peut pas être supprimé");
        }
        productRepository.deleteById(id);
    }
}
