package com.hackaton.vente.sale.service.stock;

import com.hackaton.vente.stock.client.MovementKind;

public record StockMove(MovementKind kind, String reason, String compensationReason) {

    public MovementKind compensationKind() {
        return kind == MovementKind.IN ? MovementKind.OUT : MovementKind.IN;
    }
}
