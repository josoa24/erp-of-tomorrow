package com.hackaton.rh.leave.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.service.name.EmployeeNameDirectory;
import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.mapper.LeaveMapper;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeaveReaderServiceTest {

    @Mock
    private LeaveRequestRepository leaveRepository;
    @Mock
    private EmployeeNameDirectory nameDirectory;

    private LeaveReaderService reader;

    @BeforeEach
    void setUp() {
        reader = new LeaveReaderService(leaveRepository, new LeaveMapper(), nameDirectory);
    }

    @Test
    void shouldKeepLeavesCoveringTheDayBoundsIncluded_whenFilteringByActiveDay() {
        when(nameDirectory.namesById()).thenReturn(Map.of(7L, "Nirina Rabe", 8L, "Toky Rakoto"));
        when(leaveRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(
                leave(1L, 7L, 1, 3), leave(2L, 8L, 3, 5), leave(3L, 7L, 4, 6)));

        List<LeaveResponse> active = reader.search(null, null, LocalDate.of(2026, 12, 3));

        assertEquals(List.of(1L, 2L), active.stream().map(LeaveResponse::id).toList());
        assertEquals("Toky Rakoto", active.get(1).employeeName());
    }

    @Test
    void shouldReturnEveryLeave_whenNoFilterIsGiven() {
        when(nameDirectory.namesById()).thenReturn(Map.of(7L, "Nirina Rabe"));
        when(leaveRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(leave(1L, 7L, 1, 3)));

        assertEquals(1, reader.search(null, null, null).size());
    }

    @Test
    void shouldNameTheEmployee_whenReadingOneLeave() {
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave(1L, 7L, 1, 3)));
        when(nameDirectory.nameOf(7L)).thenReturn("Nirina Rabe");

        assertEquals("Nirina Rabe", reader.findById(1L).employeeName());
    }

    @Test
    void shouldThrowNotFound_whenLeaveDoesNotExist() {
        when(leaveRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reader.findById(9L));
    }

    private LeaveRequest leave(Long id, Long employeeId, int startDay, int endDay) {
        LeaveRequest leave = new LeaveRequest();
        leave.setId(id);
        leave.setEmployeeId(employeeId);
        leave.setStartDate(LocalDate.of(2026, 12, startDay));
        leave.setEndDate(LocalDate.of(2026, 12, endDay));
        leave.setStatus(LeaveStatus.VALIDATED);
        return leave;
    }
}
