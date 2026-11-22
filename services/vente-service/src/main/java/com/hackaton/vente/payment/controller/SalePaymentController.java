package com.hackaton.vente.payment.controller;

import com.hackaton.vente.common.security.GatewayIdentityFilter;
import com.hackaton.vente.payment.dto.PaymentRequest;
import com.hackaton.vente.payment.dto.PaymentResponse;
import com.hackaton.vente.payment.service.reader.PaymentReader;
import com.hackaton.vente.payment.service.recorder.PaymentRecorder;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales/{saleId}/payments")
@RequiredArgsConstructor
public class SalePaymentController {

    private final PaymentRecorder paymentRecorder;
    private final PaymentReader paymentReader;

    @GetMapping
    public List<PaymentResponse> findBySale(@PathVariable Long saleId) {
        return paymentReader.findBySale(saleId);
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> record(
            @PathVariable Long saleId,
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader(GatewayIdentityFilter.USER_ID) Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentRecorder.record(saleId, request, userId));
    }
}
