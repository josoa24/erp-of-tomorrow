package com.hackaton.rh.employee.controller;

import static com.hackaton.rh.common.GatewayRequests.postJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.dto.EmployeeRequest;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.service.creator.EmployeeCreator;
import com.hackaton.rh.employee.service.deleter.EmployeeDeleter;
import com.hackaton.rh.employee.service.reader.EmployeeReader;
import com.hackaton.rh.employee.service.updater.EmployeeUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerCreateTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmployeeReader employeeReader;
    @MockitoBean
    private EmployeeCreator employeeCreator;
    @MockitoBean
    private EmployeeUpdater employeeUpdater;
    @MockitoBean
    private EmployeeDeleter employeeDeleter;

    @Test
    void shouldReturn201WithEmployee_whenCreatingValidEmployee() throws Exception {
        EmployeeResponse jean = EmployeeFixtures.jean();
        when(employeeCreator.create(any(EmployeeRequest.class))).thenReturn(jean);

        mockMvc.perform(postJson("/employees", EmployeeFixtures.VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.leaveBalance").value(25));
    }

    @Test
    void shouldReturn400_whenCreatingEmployeeWithInvalidEmail() throws Exception {
        mockMvc.perform(postJson("/employees", EmployeeFixtures.INVALID_EMAIL_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenHireDateIsMalformed() throws Exception {
        String body = EmployeeFixtures.VALID_BODY.replace("2025-01-15", "15/01/2025");

        mockMvc.perform(postJson("/employees", body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenDepartmentDoesNotExist() throws Exception {
        when(employeeCreator.create(any(EmployeeRequest.class)))
                .thenThrow(new ResourceNotFoundException("Département", 3L));

        mockMvc.perform(postJson("/employees", EmployeeFixtures.VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn409_whenEmailIsAlreadyUsed() throws Exception {
        when(employeeCreator.create(any(EmployeeRequest.class))).thenThrow(new DuplicateResourceException("existe"));

        mockMvc.perform(postJson("/employees", EmployeeFixtures.VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
