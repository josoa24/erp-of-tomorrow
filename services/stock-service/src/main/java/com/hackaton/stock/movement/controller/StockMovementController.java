package com.hackaton.stock.movement.controller;

import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.service.creator.StockMovementCreator;
import com.hackaton.stock.movement.service.reader.StockMovementReader;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products/{productId}/movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementCreator movementCreator;
    private final StockMovementReader movementReader;

    @GetMapping
    public List<MovementResponse> findByProduct(@PathVariable Long productId) {
        return movementReader.findByProductId(productId);
    }

    @PostMapping
    public ResponseEntity<MovementResponse> record(
            @PathVariable Long productId, @Valid @RequestBody MovementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementCreator.record(productId, request));
    }
}
