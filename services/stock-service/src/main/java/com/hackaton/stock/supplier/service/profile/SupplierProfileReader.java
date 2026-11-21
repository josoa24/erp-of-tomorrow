package com.hackaton.stock.supplier.service.profile;

import com.hackaton.stock.supplier.dto.SupplierProfileResponse;

public interface SupplierProfileReader {

    SupplierProfileResponse findProfile(Long supplierId);
}
