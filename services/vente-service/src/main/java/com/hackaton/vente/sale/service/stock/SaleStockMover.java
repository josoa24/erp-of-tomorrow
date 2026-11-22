package com.hackaton.vente.sale.service.stock;

import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.stock.client.StockClient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaleStockMover {

    private final StockClient stockClient;

    public <T> T moveThen(List<SaleLine> lines, StockMove move, Supplier<T> completion) {
        List<SaleLine> moved = new ArrayList<>();
        try {
            for (SaleLine line : lines) {
                stockClient.recordMovement(line.getProductId(), move.kind(), line.getQuantity(), move.reason());
                moved.add(line);
            }
            return completion.get();
        } catch (RuntimeException ex) {
            compensate(moved, move);
            throw ex;
        }
    }

    private void compensate(List<SaleLine> moved, StockMove move) {
        for (SaleLine line : moved) {
            try {
                stockClient.recordMovement(
                        line.getProductId(), move.compensationKind(), line.getQuantity(), move.compensationReason());
            } catch (RuntimeException ex) {
                log.error("Compensation du stock impossible pour le produit {} ({})",
                        line.getProductId(), move.reason(), ex);
            }
        }
    }
}
