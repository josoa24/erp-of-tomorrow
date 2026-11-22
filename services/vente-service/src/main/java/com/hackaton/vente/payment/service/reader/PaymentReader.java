package com.hackaton.vente.payment.service.reader;

import com.hackaton.vente.payment.dto.PaymentResponse;
import java.util.List;

public interface PaymentReader {

    List<PaymentResponse> findBySale(Long saleId);
}
