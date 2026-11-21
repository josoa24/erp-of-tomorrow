package com.hackaton.stock.purchaseorder.service.reader;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.mapper.PurchaseOrderMapper;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseOrderReaderService implements PurchaseOrderReader {

    private final PurchaseOrderRepository orderRepository;
    private final PurchaseOrderMapper orderMapper;

    @Override
    public PurchaseOrderResponse findById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Bon de commande", id));
    }

    @Override
    public List<PurchaseOrderResponse> search(Long supplierId, OrderStatus status) {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(order -> supplierId == null || Objects.equals(order.getSupplierId(), supplierId))
                .filter(order -> status == null || order.getStatus() == status)
                .map(orderMapper::toResponse)
                .toList();
    }
}
