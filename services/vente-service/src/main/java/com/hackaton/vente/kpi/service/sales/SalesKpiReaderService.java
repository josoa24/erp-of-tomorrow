package com.hackaton.vente.kpi.service.sales;

import com.hackaton.vente.common.time.LocalDays;
import com.hackaton.vente.kpi.dto.SalesKpiResponse;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesKpiReaderService implements SalesKpiReader {

    private static final int DAYS_IN_WEEK = 7;

    private final SaleRepository saleRepository;
    private final SalePaymentRepository paymentRepository;
    private final Clock clock;

    @Override
    public SalesKpiResponse read() {
        ZoneId zone = clock.getZone();
        LocalDate today = LocalDate.now(clock);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate previousStart = monthStart.minusMonths(1);
        int elapsedDays = Math.min(today.getDayOfMonth(), previousStart.lengthOfMonth());
        LocalDate previousEnd = previousStart.plusDays(elapsedDays - 1L);
        LocalDate lastWeekDay = today.minusDays(DAYS_IN_WEEK);
        List<Sale> sales = saleRepository.findEffectiveValidatedSince(LocalDays.startOf(previousStart, zone));
        SalesTally month = SalesTally.of(sales, monthStart, today, zone);
        SalesTally previousMonth = SalesTally.of(sales, previousStart, previousEnd, zone);
        SalesTally todayTally = SalesTally.of(sales, today, today, zone);
        BigDecimal collected = paymentRepository.sumAmountPaidBetween(
                LocalDays.startOf(today, zone), LocalDays.startOf(today.plusDays(1), zone));
        return new SalesKpiResponse(
                month.revenue(),
                previousMonth.revenue(),
                todayTally.count(),
                todayTally.revenue(),
                collected,
                SalesTally.of(sales, lastWeekDay, lastWeekDay, zone).revenue(),
                month.averageBasket(),
                previousMonth.averageBasket());
    }
}
