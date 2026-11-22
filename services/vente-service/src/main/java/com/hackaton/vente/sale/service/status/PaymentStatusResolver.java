package com.hackaton.vente.sale.service.status;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.common.time.LocalDays;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStatusResolver {

    private final Clock clock;

    public LocalDate dueDate(Sale sale) {
        if (sale.getValidatedAt() == null) {
            return null;
        }
        LocalDate validatedDay = LocalDays.dayOf(sale.getValidatedAt(), clock.getZone());
        boolean onCredit = sale.getPaymentMethod() == PaymentMethod.CREDIT && sale.getCreditDays() != null;
        return onCredit ? validatedDay.plusDays(sale.getCreditDays()) : validatedDay;
    }

    public PaymentStatus resolve(Sale sale, BigDecimal paidAmount, boolean cancelled) {
        if (cancelled) {
            return PaymentStatus.CANCELLED;
        }
        if (sale.getStatus() == DocumentStatus.DRAFT) {
            return PaymentStatus.DRAFT;
        }
        if (paidAmount.compareTo(sale.getTotalAmount()) >= 0) {
            return PaymentStatus.PAID;
        }
        LocalDate dueDate = dueDate(sale);
        boolean overdue = dueDate != null && LocalDate.now(clock).isAfter(dueDate);
        return overdue ? PaymentStatus.OVERDUE : PaymentStatus.TO_COLLECT;
    }
}
