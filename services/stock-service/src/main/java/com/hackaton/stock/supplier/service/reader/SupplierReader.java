package com.hackaton.stock.supplier.service.reader;

import com.hackaton.stock.supplier.dto.SupplierResponse;
import java.util.List;

public interface SupplierReader {

    SupplierResponse findById(Long id);

    List<SupplierResponse> findAll();
}
