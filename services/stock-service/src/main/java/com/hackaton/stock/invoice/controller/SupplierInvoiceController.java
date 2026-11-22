package com.hackaton.stock.invoice.controller;

import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.service.creator.SupplierInvoiceCreator;
import com.hackaton.stock.invoice.service.reader.SupplierInvoiceReader;
import com.hackaton.stock.invoice.shield.InvoiceShield;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supplier-invoices")
@RequiredArgsConstructor
public class SupplierInvoiceController {

    private final InvoiceShield invoiceShield;
    private final SupplierInvoiceCreator invoiceCreator;
    private final SupplierInvoiceReader invoiceReader;

    @Operation(summary = "Contrôle Invoice Shield sans enregistrer la facture")
    @PostMapping("/check")
    public ShieldReport check(@Valid @RequestBody SupplierInvoiceRequest request) {
        return invoiceShield.inspect(request);
    }

    @Operation(summary = "Enregistre une facture fournisseur après contrôle (CLEARED ou BLOCKED)")
    @PostMapping
    public ResponseEntity<SupplierInvoiceResponse> create(
            @Valid @RequestBody SupplierInvoiceRequest request,
            @Parameter(hidden = true) @RequestHeader(ActorHeaders.ID) Long userId,
            @Parameter(hidden = true) @RequestHeader(value = ActorHeaders.EMAIL, required = false) String email,
            @Parameter(hidden = true) @RequestHeader(ActorHeaders.ROLE) String role) {
        Actor actor = new Actor(userId, email, role);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceCreator.create(request, actor));
    }

    @GetMapping
    public List<SupplierInvoiceResponse> search(@RequestParam(required = false) InvoiceStatus status,
                                                @RequestParam(required = false) Long supplierId) {
        return invoiceReader.search(status, supplierId);
    }

    @GetMapping("/{id}")
    public SupplierInvoiceResponse findById(@PathVariable Long id) {
        return invoiceReader.findById(id);
    }
}
