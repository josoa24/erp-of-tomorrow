package com.hackaton.vente.cancellation.controller;

import com.hackaton.vente.cancellation.dto.CancellationRequest;
import com.hackaton.vente.cancellation.service.SaleCanceller;
import com.hackaton.vente.common.security.GatewayIdentityFilter;
import com.hackaton.vente.sale.dto.SaleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleCancellationController {

    private final SaleCanceller saleCanceller;

    @PostMapping("/{id}/cancel")
    public SaleResponse cancel(
            @PathVariable Long id,
            @Valid @RequestBody CancellationRequest request,
            @RequestHeader(GatewayIdentityFilter.USER_ID) Long userId) {
        return saleCanceller.cancel(id, request, userId);
    }
}
