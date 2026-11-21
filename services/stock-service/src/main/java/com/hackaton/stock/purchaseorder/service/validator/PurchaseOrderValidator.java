package com.hackaton.stock.purchaseorder.service.validator;

import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;

public interface PurchaseOrderValidator {

    PurchaseOrderResponse validate(Long id);
}
