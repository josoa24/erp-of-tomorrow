package com.hackaton.stock.supplier.service.updater;

import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;

public interface SupplierUpdater {

    SupplierResponse update(Long id, SupplierRequest request);
}
