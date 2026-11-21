package com.hackaton.vente.sale.service.creator;

import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.dto.SaleResponse;

public interface SaleCreator {

    SaleResponse create(SaleRequest request);
}
