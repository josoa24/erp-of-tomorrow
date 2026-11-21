package com.hackaton.stock.invoice.controller;

import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.ApprovalRequest;
import com.hackaton.stock.invoice.dto.InvoiceEventResponse;
import com.hackaton.stock.invoice.dto.RejectionRequest;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.service.decision.InvoiceDecider;
import com.hackaton.stock.invoice.service.reader.SupplierInvoiceReader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supplier-invoices/{id}")
@RequiredArgsConstructor
public class InvoiceDecisionController {

    private final InvoiceDecider invoiceDecider;
    private final SupplierInvoiceReader invoiceReader;

    @Operation(summary = "Approuve une facture. Facture BLOCKED : ADMIN (autre que l'auteur) et justification")
    @PostMapping("/approve")
    public SupplierInvoiceResponse approve(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) ApprovalRequest request,
            @Parameter(hidden = true) @RequestHeader(ActorHeaders.ID) Long userId,
            @Parameter(hidden = true) @RequestHeader(value = ActorHeaders.EMAIL, required = false) String email,
            @Parameter(hidden = true) @RequestHeader(ActorHeaders.ROLE) String role) {
        String justification = request == null ? null : request.justification();
        return invoiceDecider.approve(id, justification, new Actor(userId, email, role));
    }

    @Operation(summary = "Rejette une facture avec un motif")
    @PostMapping("/reject")
    public SupplierInvoiceResponse reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectionRequest request,
            @Parameter(hidden = true) @RequestHeader(ActorHeaders.ID) Long userId,
            @Parameter(hidden = true) @RequestHeader(value = ActorHeaders.EMAIL, required = false) String email,
            @Parameter(hidden = true) @RequestHeader(ActorHeaders.ROLE) String role) {
        return invoiceDecider.reject(id, request.reason(), new Actor(userId, email, role));
    }

    @Operation(summary = "Journal d'audit de la facture : création, contrôle, décision")
    @GetMapping("/history")
    public List<InvoiceEventResponse> history(@PathVariable Long id) {
        return invoiceReader.history(id);
    }
}
