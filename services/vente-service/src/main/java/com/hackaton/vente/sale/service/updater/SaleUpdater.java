package com.hackaton.vente.sale.service.updater;

import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.dto.SaleResponse;

public interface SaleUpdater {

    SaleResponse update(Long id, SaleRequest request);
}
