package com.hackaton.stock.purchaseorder.repository;

import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    boolean existsBySupplierId(Long supplierId);

    boolean existsByLinesProductId(Long productId);

    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();

    List<PurchaseOrder> findBySupplierIdOrderByCreatedAtDesc(Long supplierId);
}
