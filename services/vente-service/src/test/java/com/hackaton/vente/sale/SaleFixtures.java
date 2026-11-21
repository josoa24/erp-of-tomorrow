package com.hackaton.vente.sale;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import java.math.BigDecimal;

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
}
