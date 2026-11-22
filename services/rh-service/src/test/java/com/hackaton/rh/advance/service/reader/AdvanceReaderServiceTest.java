package com.hackaton.rh.advance.service.reader;

import static com.hackaton.rh.advance.AdvanceTestData.DECEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.NOVEMBER;
import static com.hackaton.rh.advance.AdvanceTestData.advance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.mapper.AdvanceMapper;
import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdvanceReaderServiceTest {

    @Mock
    private SalaryAdvanceRepository advanceRepository;
    @Mock
    private EmployeeNameDirectory nameDirectory;

    private AdvanceReaderService reader;

    @BeforeEach
    void setUp() {
        when(nameDirectory.namesById()).thenReturn(Map.of(7L, "Toky Rakoto", 8L, "Nirina Rabe"));
        when(advanceRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(
                advance(1L, 7L, NOVEMBER, "150000", AdvanceStatus.VALIDATED),
                advance(2L, 8L, NOVEMBER, "80000", AdvanceStatus.DRAFT),
                advance(3L, 7L, DECEMBER, "50000", AdvanceStatus.DRAFT)));
        reader = new AdvanceReaderService(advanceRepository, new AdvanceMapper(), nameDirectory);
    }

    @Test
    void shouldKeepOnlyAdvancesOfTheMonth_whenFilteringByMonth() {
        List<AdvanceResponse> advances = reader.search(YearMonth.of(2026, 11), null, null);

        assertEquals(List.of(1L, 2L), advances.stream().map(AdvanceResponse::id).toList());
        assertEquals("Nirina Rabe", advances.get(1).employeeName());
    }

    @Test
    void shouldCombineEmployeeAndStatusFilters_whenBothAreGiven() {
        List<AdvanceResponse> advances = reader.search(null, 7L, AdvanceStatus.DRAFT);

        assertEquals(List.of(3L), advances.stream().map(AdvanceResponse::id).toList());
    }

    @Test
    void shouldReturnEveryAdvance_whenNoFilterIsGiven() {
        assertEquals(3, reader.search(null, null, null).size());
    }
}
