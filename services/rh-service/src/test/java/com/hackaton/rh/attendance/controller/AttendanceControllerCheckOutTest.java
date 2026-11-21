package com.hackaton.rh.attendance.controller;

import static com.hackaton.rh.common.GatewayRequests.postJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.attendance.dto.AttendanceResponse;
import com.hackaton.rh.attendance.service.checkin.AttendanceCheckIn;
import com.hackaton.rh.attendance.service.checkout.AttendanceCheckOut;
import com.hackaton.rh.attendance.service.reader.AttendanceReader;
import com.hackaton.rh.common.exception.BusinessException;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerCheckOutTest {

    private static final String BODY = "{\"employeeId\":7}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AttendanceReader attendanceReader;
    @MockitoBean
    private AttendanceCheckIn attendanceCheckIn;
    @MockitoBean
    private AttendanceCheckOut attendanceCheckOut;

    @Test
    void shouldReturn200WithWorkedMinutes_whenCheckingOut() throws Exception {
        AttendanceResponse closed = new AttendanceResponse(
                4L, 7L, LocalDate.of(2026, 11, 20), Instant.EPOCH, Instant.EPOCH.plusSeconds(28800), 480L);
        when(attendanceCheckOut.checkOut(7L)).thenReturn(closed);

        mockMvc.perform(postJson("/attendances/check-out", BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workedMinutes").value(480));
    }

    @Test
    void shouldReturn400_whenEmployeeIdIsMissingAtCheckOut() throws Exception {
        mockMvc.perform(postJson("/attendances/check-out", "{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn422_whenNoAttendanceIsOpen() throws Exception {
        when(attendanceCheckOut.checkOut(7L)).thenThrow(new BusinessException("Aucun pointage ouvert"));

        mockMvc.perform(postJson("/attendances/check-out", BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
