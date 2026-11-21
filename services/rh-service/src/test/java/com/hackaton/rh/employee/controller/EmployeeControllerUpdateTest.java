package com.hackaton.rh.employee.controller;

import static com.hackaton.rh.common.GatewayRequests.putJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class EmployeeControllerUpdateTest {

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
    void shouldReturn200WithEmployee_whenUpdatingValidEmployee() throws Exception {
        EmployeeResponse jean = EmployeeFixtures.jean();
        when(employeeUpdater.update(eq(5L), any(EmployeeRequest.class))).thenReturn(jean);

        mockMvc.perform(putJson("/employees/{id}", EmployeeFixtures.VALID_BODY, 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jean"));
    }

    @Test
    void shouldReturn400_whenUpdatingEmployeeWithInvalidEmail() throws Exception {
        mockMvc.perform(putJson("/employees/{id}", EmployeeFixtures.INVALID_EMAIL_BODY, 5L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenUpdatingUnknownEmployee() throws Exception {
        when(employeeUpdater.update(eq(9L), any(EmployeeRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employé", 9L));

        mockMvc.perform(putJson("/employees/{id}", EmployeeFixtures.VALID_BODY, 9L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn409_whenUpdatingToEmailAlreadyUsed() throws Exception {
        when(employeeUpdater.update(eq(5L), any(EmployeeRequest.class)))
                .thenThrow(new DuplicateResourceException("existe"));

        mockMvc.perform(putJson("/employees/{id}", EmployeeFixtures.VALID_BODY, 5L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
