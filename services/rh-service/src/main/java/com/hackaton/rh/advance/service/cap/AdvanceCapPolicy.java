package com.hackaton.rh.advance.service.cap;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.employee.entity.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdvanceCapPolicy {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final BigDecimal capPercent;

    public AdvanceCapPolicy(@Value("${rh.advance.cap-percent:30}") BigDecimal capPercent) {
        this.capPercent = capPercent;
    }

    public BigDecimal capOf(BigDecimal baseSalary) {
        return baseSalary.multiply(capPercent).divide(HUNDRED, 2, RoundingMode.DOWN);
    }

    public void check(Employee employee, BigDecimal committed, BigDecimal amount) {
        BigDecimal cap = capOf(employee.getBaseSalary());
        if (committed.add(amount).compareTo(cap) <= 0) {
            return;
        }
        BigDecimal remaining = cap.subtract(committed).max(BigDecimal.ZERO);
        throw new BusinessException("Plafond d'avance dépassé pour " + employee.fullName() + " : "
                + ariary(amount) + " demandés, il reste " + ariary(remaining) + " sur " + ariary(cap)
                + " ce mois-ci (" + capPercent.stripTrailingZeros().toPlainString() + " % du salaire)");
    }

    private String ariary(BigDecimal amount) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.FRANCE);
        format.setMaximumFractionDigits(2);
        return format.format(amount) + " Ar";
    }
}
