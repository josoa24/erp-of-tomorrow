package com.hackaton.stock.purchaseorder.service.receiver;

import com.hackaton.stock.common.exception.BusinessException;
import com.hackaton.stock.common.exception.ImmutableResourceException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.service.creator.StockMovementCreator;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import com.hackaton.stock.purchaseorder.mapper.PurchaseOrderMapper;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderReceiverService implements PurchaseOrderReceiver {

    private final PurchaseOrderRepository orderRepository;
    private final StockMovementCreator movementCreator;
    private final PurchaseOrderMapper orderMapper;

    @Override
    @Transactional
    public PurchaseOrderResponse receive(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de commande", id));
        ensureReceivable(order);
        for (PurchaseOrderLine line : order.getLines()) {
            movementCreator.record(
                    line.getProductId(),
                    new MovementRequest(MovementType.IN, line.getQuantity(), "Réception commande #" + id));
        }
        order.setStatus(OrderStatus.RECEIVED);
        order.setReceivedAt(Instant.now());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private void ensureReceivable(PurchaseOrder order) {
        if (order.getStatus() == OrderStatus.RECEIVED) {
            throw new ImmutableResourceException("Le bon de commande " + order.getId() + " est déjà réceptionné");
        }
        if (order.getStatus() == OrderStatus.DRAFT) {
            throw new BusinessException("Le bon de commande doit être validé avant la réception");
        }
    }
}
