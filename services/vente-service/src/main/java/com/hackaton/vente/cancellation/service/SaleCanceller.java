package com.hackaton.vente.cancellation.service;

import com.hackaton.vente.cancellation.dto.CancellationRequest;
import com.hackaton.vente.sale.dto.SaleResponse;

public interface SaleCanceller {

    SaleResponse cancel(Long saleId, CancellationRequest request, Long cancelledBy);
}
