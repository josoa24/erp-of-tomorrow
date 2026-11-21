package com.hackaton.stock.purchaseorder.service.validator;

import com.hackaton.stock.common.exception.ImmutableResourceException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.mapper.PurchaseOrderMapper;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderValidatorService implements PurchaseOrderValidator {

    private final PurchaseOrderRepository orderRepository;
    private final PurchaseOrderMapper orderMapper;

    @Override
    @Transactional
    public PurchaseOrderResponse validate(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de commande", id));
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new ImmutableResourceException("Le bon de commande " + id + " est déjà validé");
        }
        order.setStatus(OrderStatus.VALIDATED);
        order.setValidatedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }
}
