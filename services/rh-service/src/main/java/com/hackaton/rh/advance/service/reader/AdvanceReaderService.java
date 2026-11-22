package com.hackaton.rh.advance.service.reader;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.mapper.AdvanceMapper;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvanceReaderService implements AdvanceReader {

    private final SalaryAdvanceRepository advanceRepository;
    private final AdvanceMapper advanceMapper;
    private final EmployeeNameDirectory nameDirectory;

    @Override
    public List<AdvanceResponse> search(YearMonth month, Long employeeId, AdvanceStatus status) {
        LocalDate monthStart = month == null ? null : month.atDay(1);
        Map<Long, String> names = nameDirectory.namesById();
        return advanceRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(advance -> monthStart == null || monthStart.equals(advance.getMonth()))
                .filter(advance -> employeeId == null || Objects.equals(advance.getEmployeeId(), employeeId))
                .filter(advance -> status == null || advance.getStatus() == status)
                .map(advance -> advanceMapper.toResponse(advance, names.get(advance.getEmployeeId())))
                .toList();
    }
}
