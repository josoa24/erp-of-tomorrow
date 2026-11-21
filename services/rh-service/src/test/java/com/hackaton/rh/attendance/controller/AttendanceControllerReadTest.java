package com.hackaton.rh.attendance.controller;

import static com.hackaton.rh.common.GatewayRequests.get;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.service.checkin.AttendanceCheckIn;
import com.hackaton.rh.attendance.service.checkout.AttendanceCheckOut;
import com.hackaton.rh.attendance.service.reader.AttendanceReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerReadTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AttendanceReader attendanceReader;
    @MockitoBean
    private AttendanceCheckIn attendanceCheckIn;
    @MockitoBean
    private AttendanceCheckOut attendanceCheckOut;

    @Test
    void shouldReturn200WithAttendances_whenSearchingByEmployeeAndDate() throws Exception {
        LocalDate day = LocalDate.of(2026, 11, 20);
        AttendanceResponse attendance = new AttendanceResponse(4L, 7L, day, Instant.EPOCH, Instant.EPOCH, 480L);
        when(attendanceReader.search(7L, day)).thenReturn(List.of(attendance));

        mockMvc.perform(get("/attendances").param("employeeId", "7").param("date", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].workedMinutes").value(480));
    }

    @Test
    void shouldReturn400_whenDateIsMalformed() throws Exception {
        mockMvc.perform(get("/attendances").param("date", "20/11/2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(attendanceReader);
    }
}
