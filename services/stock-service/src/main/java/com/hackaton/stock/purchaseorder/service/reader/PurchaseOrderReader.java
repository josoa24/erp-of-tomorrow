package com.hackaton.stock.purchaseorder.service.reader;

import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import java.util.List;

public interface PurchaseOrderReader {

    PurchaseOrderResponse findById(Long id);

    List<PurchaseOrderResponse> search(Long supplierId, OrderStatus status);
}
