package com.hackaton.stock.supplier.service.creator;

import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;

public interface SupplierCreator {

    SupplierResponse create(SupplierRequest request);
}
