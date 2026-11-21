package com.hackaton.stock.purchaseorder.service.receiver;

import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;

public interface PurchaseOrderReceiver {

    PurchaseOrderResponse receive(Long id);
}
