package com.hackaton.stock.supplier.service.profile;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.supplier.dto.RecentOrder;
import com.hackaton.stock.supplier.dto.SuppliedProduct;
import com.hackaton.stock.supplier.dto.SupplierProfileResponse;
import com.hackaton.stock.supplier.entity.Supplier;
import com.hackaton.stock.supplier.mapper.SupplierMapper;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierProfileReaderService implements SupplierProfileReader {

    private static final int RECENT_ORDERS = 5;
    private static final long MINUTES_PER_DAY = 1440L;

    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository orderRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierProfileResponse findProfile(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", supplierId));
        List<PurchaseOrder> orders = orderRepository.findBySupplierIdOrderByCreatedAtDesc(supplierId);
        List<PurchaseOrder> committed = orders.stream().filter(order -> order.getStatus() != OrderStatus.DRAFT).toList();
        List<PurchaseOrder> received = orders.stream().filter(order -> order.getStatus() == OrderStatus.RECEIVED).toList();
        return new SupplierProfileResponse(
                supplierMapper.toResponse(supplier),
                orders.size(),
                received.size(),
                committed.stream().map(PurchaseOrder::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                averageReceptionDays(received),
                suppliedProducts(committed),
                orders.stream().limit(RECENT_ORDERS)
                        .map(order -> new RecentOrder(
                                order.getId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt()))
                        .toList());
    }

    private BigDecimal averageReceptionDays(List<PurchaseOrder> received) {
        List<PurchaseOrder> measurable = received.stream()
                .filter(order -> order.getValidatedAt() != null && order.getReceivedAt() != null)
                .toList();
        if (measurable.isEmpty()) {
            return null;
        }
        long minutes = measurable.stream()
                .mapToLong(order -> Duration.between(order.getValidatedAt(), order.getReceivedAt()).toMinutes())
                .sum();
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(measurable.size() * MINUTES_PER_DAY), 1, RoundingMode.HALF_UP);
    }

    private List<SuppliedProduct> suppliedProducts(List<PurchaseOrder> committed) {
        Map<Long, List<PurchaseOrderLine>> byProduct = committed.stream()
                .flatMap(order -> order.getLines().stream())
                .collect(Collectors.groupingBy(PurchaseOrderLine::getProductId));
        return byProduct.values().stream()
                .map(lines -> new SuppliedProduct(
                        lines.get(0).getProductId(),
                        lines.get(0).getProductName(),
                        lines.stream().mapToLong(PurchaseOrderLine::getQuantity).sum()))
                .sorted(Comparator.comparingLong(SuppliedProduct::totalQuantity).reversed())
                .toList();
    }
}
