package com.hackaton.rh.advance.service.summary;

import com.hackaton.rh.advance.dto.AdvanceSummaryResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvanceSummaryReaderService implements AdvanceSummaryReader {

    private final SalaryAdvanceRepository advanceRepository;
    private final Clock clock;

    @Override
    public AdvanceSummaryResponse summarize() {
        List<SalaryAdvance> advances = advanceRepository.findAllByMonthAndStatusIn(
                YearMonth.now(clock).atDay(1), AdvanceStatus.COMMITTED);
        BigDecimal total = advances.stream().map(SalaryAdvance::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long pending = advances.stream().filter(advance -> advance.getStatus() == AdvanceStatus.DRAFT).count();
        return new AdvanceSummaryResponse(total, advances.size(), pending);
    }
}
