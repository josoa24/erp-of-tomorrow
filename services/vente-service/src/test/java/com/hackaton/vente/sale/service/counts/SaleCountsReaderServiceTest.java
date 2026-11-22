package com.hackaton.vente.sale.service.counts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.controller.SaleResponseFixtures;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleCountsResponse;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.service.reader.SaleReader;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleCountsReaderServiceTest {

    @Mock
    private SaleReader saleReader;
    @InjectMocks
    private SaleCountsReaderService service;

    private SaleResponse sale(Long id, PaymentStatus status, PaymentMethod method) {
        SaleResponse base = SaleResponseFixtures.sale(id, DocumentStatus.VALIDATED, status);
        return new SaleResponse(base.id(), base.number(), base.customerId(), base.customerName(), base.status(),
                status, base.totalAmount(), base.paidAmount(), method, null, null, false, base.lines(), null, null);
    }

    @Test
    void shouldCountEachFilter_whenSalesHaveMixedStatuses() {
        LocalDate from = LocalDate.of(2026, 11, 1);
        LocalDate to = LocalDate.of(2026, 11, 30);
        List<SaleResponse> sales = List.of(
                sale(1L, PaymentStatus.PAID, PaymentMethod.MVOLA),
                sale(2L, PaymentStatus.TO_COLLECT, PaymentMethod.CREDIT),
                sale(3L, PaymentStatus.OVERDUE, PaymentMethod.CREDIT),
                sale(4L, PaymentStatus.CANCELLED, PaymentMethod.CASH),
                sale(5L, PaymentStatus.DRAFT, null));
        when(saleReader.search(SaleSearchCriteria.between(from, to), null, null))
                .thenReturn(new PageResult<>(sales, sales.size()));

        SaleCountsResponse counts = service.count(from, to);

        assertEquals(new SaleCountsResponse(5, 2, 1, 1, 1, 2, 1), counts);
    }
}
