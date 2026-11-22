package com.hackaton.vente.sale.service.counts;

import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleCountsResponse;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.service.reader.SaleReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleCountsReaderService implements SaleCountsReader {

    private final SaleReader saleReader;

    @Override
    public SaleCountsResponse count(LocalDate from, LocalDate to) {
        List<SaleResponse> sales = saleReader.search(SaleSearchCriteria.between(from, to), null, null).items();
        return new SaleCountsResponse(
                sales.size(),
                count(sales, withStatus(PaymentStatus.TO_COLLECT, PaymentStatus.OVERDUE)),
                count(sales, withStatus(PaymentStatus.PAID)),
                count(sales, withStatus(PaymentStatus.CANCELLED)),
                count(sales, withStatus(PaymentStatus.OVERDUE)),
                count(sales, sale -> sale.paymentMethod() == PaymentMethod.CREDIT),
                count(sales, withStatus(PaymentStatus.DRAFT)));
    }

    private Predicate<SaleResponse> withStatus(PaymentStatus... statuses) {
        Set<PaymentStatus> accepted = Set.of(statuses);
        return sale -> accepted.contains(sale.paymentStatus());
    }

    private long count(List<SaleResponse> sales, Predicate<SaleResponse> predicate) {
        return sales.stream().filter(predicate).count();
    }
}
