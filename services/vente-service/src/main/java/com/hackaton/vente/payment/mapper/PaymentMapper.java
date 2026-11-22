package com.hackaton.vente.payment.mapper;

import com.hackaton.vente.payment.dto.PaymentResponse;
import com.hackaton.vente.payment.entity.SalePayment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(SalePayment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getSale().getId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getPaidAt(),
                payment.getReference(),
                payment.getRecordedBy());
    }
}
