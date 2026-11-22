package com.hackaton.vente.sale;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.SaleLineRequest;
import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class SaleFixtures {

    private SaleFixtures() {
    }

    public static SaleLine line(Long productId, String name, int quantity, String price) {
        SaleLine line = new SaleLine();
        line.setProductId(productId);
        line.setProductName(name);
        line.setQuantity(quantity);
        line.setUnitPrice(new BigDecimal(price));
        return line;
    }

    public static Sale draftSale(Long id, SaleLine... lines) {
        Sale sale = new Sale();
        sale.setId(id);
        sale.setStatus(DocumentStatus.DRAFT);
        for (SaleLine line : lines) {
            sale.getLines().add(line);
            sale.setTotalAmount(sale.getTotalAmount().add(line.lineTotal()));
        }
        return sale;
    }

    public static Sale validatedSale(Long id, String validatedAt, PaymentMethod method, SaleLine... lines) {
        Sale sale = draftSale(id, lines);
        sale.setStatus(DocumentStatus.VALIDATED);
        sale.setCreatedAt(Instant.parse(validatedAt));
        sale.setValidatedAt(Instant.parse(validatedAt));
        sale.setPaymentMethod(method);
        return sale;
    }

    public static Sale withId(Sale sale, Long id) {
        sale.setId(id);
        return sale;
    }

    public static SaleRequest request(Long customerId, Long productId, int quantity) {
        return new SaleRequest(customerId, List.of(new SaleLineRequest(productId, quantity)), null, null, null);
    }
}
