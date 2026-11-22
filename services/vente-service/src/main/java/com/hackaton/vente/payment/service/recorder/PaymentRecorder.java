package com.hackaton.vente.payment.service.recorder;

import com.hackaton.vente.payment.dto.PaymentRequest;
import com.hackaton.vente.payment.dto.PaymentResponse;

public interface PaymentRecorder {

    PaymentResponse record(Long saleId, PaymentRequest request, Long recordedBy);
}
