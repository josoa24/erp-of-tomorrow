package com.hackaton.stock.purchaseorder.controller;

import com.hackaton.stock.purchaseorder.dto.PurchaseOrderRequest;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.service.creator.PurchaseOrderCreator;
import com.hackaton.stock.purchaseorder.service.reader.PurchaseOrderReader;
import com.hackaton.stock.purchaseorder.service.receiver.PurchaseOrderReceiver;
import com.hackaton.stock.purchaseorder.service.validator.PurchaseOrderValidator;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderReader orderReader;
    private final PurchaseOrderCreator orderCreator;
    private final PurchaseOrderValidator orderValidator;
    private final PurchaseOrderReceiver orderReceiver;

    @GetMapping
    public List<PurchaseOrderResponse> search(
            @RequestParam(required = false) Long supplierId, @RequestParam(required = false) OrderStatus status) {
        return orderReader.search(supplierId, status);
    }

    @GetMapping("/{id}")
    public PurchaseOrderResponse findById(@PathVariable Long id) {
        return orderReader.findById(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> create(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderCreator.create(request));
    }

    @PostMapping("/{id}/validate")
    public PurchaseOrderResponse validate(@PathVariable Long id) {
        return orderValidator.validate(id);
    }

    @PostMapping("/{id}/receive")
    public PurchaseOrderResponse receive(@PathVariable Long id) {
        return orderReceiver.receive(id);
    }
}
