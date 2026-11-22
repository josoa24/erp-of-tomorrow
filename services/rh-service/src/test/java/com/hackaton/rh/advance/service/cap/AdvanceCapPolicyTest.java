package com.hackaton.rh.advance.service.cap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.rh.advance.AdvanceTestData;
import com.hackaton.rh.common.exception.BusinessException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AdvanceCapPolicyTest {

    private final AdvanceCapPolicy policy = new AdvanceCapPolicy(new BigDecimal("30"));

    @Test
    void shouldComputeThirtyPercentOfBaseSalary_whenComputingCap() {
        assertEquals(new BigDecimal("240000.00"), policy.capOf(new BigDecimal("800000")));
    }

    @Test
    void shouldRoundCapDown_whenSalaryGivesFractionOfCent() {
        assertEquals(new BigDecimal("0.03"), policy.capOf(new BigDecimal("0.11")));
    }

    @Test
    void shouldAllowAdvance_whenAmountReachesExactlyTheCap() {
        assertDoesNotThrow(() -> policy.check(AdvanceTestData.toky(), BigDecimal.ZERO, new BigDecimal("240000")));
    }

    @Test
    void shouldRefuseAdvance_whenAmountExceedsCapByOneAriary() {
        assertThrows(BusinessException.class,
                () -> policy.check(AdvanceTestData.toky(), BigDecimal.ZERO, new BigDecimal("240001")));
    }

    @Test
    void shouldCountCommittedAmount_whenCheckingRemainingCap() {
        assertDoesNotThrow(
                () -> policy.check(AdvanceTestData.toky(), new BigDecimal("150000"), new BigDecimal("90000")));
        assertThrows(BusinessException.class,
                () -> policy.check(AdvanceTestData.toky(), new BigDecimal("150000"), new BigDecimal("90001")));
    }

    @Test
    void shouldExplainRemainingAmount_whenRefusingAdvance() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> policy.check(AdvanceTestData.toky(), new BigDecimal("150000"), new BigDecimal("100000")));

        String message = error.getMessage().replace(' ', ' ').replace(' ', ' ');
        assertTrue(message.contains("Toky Rakoto"));
        assertTrue(message.contains("il reste 90 000 Ar sur 240 000 Ar"));
        assertTrue(message.contains("30 % du salaire"));
    }
}
