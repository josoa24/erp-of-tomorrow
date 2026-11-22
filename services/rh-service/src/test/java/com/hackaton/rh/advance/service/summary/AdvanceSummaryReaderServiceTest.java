package com.hackaton.rh.advance.service.summary;

import static com.hackaton.rh.advance.AdvanceTestData.DECEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.NOVEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.advance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.rh.advance.AdvanceTestData;
import com.hackaton.rh.advance.dto.AdvanceSummaryResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvanceSummaryReaderServiceTest {

    @Mock
    private SalaryAdvanceRepository advanceRepository;

    @Test
    void shouldSumCommittedAdvancesAndCountDrafts_whenSummarizingMonth() {
        when(advanceRepository.findAllByMonthAndStatusIn(NOVEMBER, AdvanceStatus.COMMITTED)).thenReturn(List.of(
                advance(1L, 7L, NOVEMBER, "150000", AdvanceStatus.VALIDATED),
                advance(2L, 8L, NOVEMBER, "80000", AdvanceStatus.DRAFT)));

        AdvanceSummaryResponse summary = summaryAt("2026-11-15T09:00:00Z").summarize();

        assertEquals(new BigDecimal("230000"), summary.monthTotal());
        assertEquals(2, summary.requests());
        assertEquals(1, summary.pending());
    }

    @Test
    void shouldReadLocalMonth_whenUtcIsStillPreviousMonth() {
        when(advanceRepository.findAllByMonthAndStatusIn(DECEMBER, AdvanceStatus.COMMITTED)).thenReturn(List.of());

        AdvanceSummaryResponse summary = summaryAt("2026-11-30T22:00:00Z").summarize();

        assertEquals(BigDecimal.ZERO, summary.monthTotal());
        assertEquals(0, summary.requests());
    }

    private AdvanceSummaryReaderService summaryAt(String instant) {
        return new AdvanceSummaryReaderService(advanceRepository, AdvanceTestData.clockAt(instant));
    }
}
