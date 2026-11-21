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
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AttendanceController.class)
class AttendanceControllerCheckInTest {

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
    void shouldReturn201WithOpenAttendance_whenCheckingIn() throws Exception {
        AttendanceResponse open = new AttendanceResponse(4L, 7L, LocalDate.of(2026, 11, 20), Instant.EPOCH, null, null);
        when(attendanceCheckIn.checkIn(7L)).thenReturn(open);

        mockMvc.perform(postJson("/attendances/check-in", BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value(7))
                .andExpect(jsonPath("$.checkOut").isEmpty());
    }

    @Test
    void shouldReturn400_whenEmployeeIdIsMissingAtCheckIn() throws Exception {
        mockMvc.perform(postJson("/attendances/check-in", "{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenCheckingInUnknownEmployee() throws Exception {
        when(attendanceCheckIn.checkIn(7L)).thenThrow(new ResourceNotFoundException("Employé", 7L));

        mockMvc.perform(postJson("/attendances/check-in", BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn422_whenAttendanceIsAlreadyOpen() throws Exception {
        when(attendanceCheckIn.checkIn(7L)).thenThrow(new BusinessException("déjà ouvert"));

        mockMvc.perform(postJson("/attendances/check-in", BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
