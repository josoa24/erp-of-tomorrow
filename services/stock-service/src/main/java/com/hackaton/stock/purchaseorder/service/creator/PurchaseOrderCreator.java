package com.hackaton.stock.purchaseorder.service.creator;

import com.hackaton.stock.purchaseorder.dto.PurchaseOrderRequest;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;

public interface PurchaseOrderCreator {

    PurchaseOrderResponse create(PurchaseOrderRequest request);
}
