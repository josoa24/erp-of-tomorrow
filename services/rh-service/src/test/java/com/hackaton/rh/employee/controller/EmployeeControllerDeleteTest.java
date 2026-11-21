package com.hackaton.rh.employee.controller;

import static com.hackaton.rh.common.GatewayRequests.delete;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
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
class EmployeeControllerDeleteTest {

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
    void shouldReturn204AndDelete_whenEmployeeHasNoHistory() throws Exception {
        mockMvc.perform(delete("/employees/{id}", 5L))
                .andExpect(status().isNoContent());

        verify(employeeDeleter).delete(5L);
    }

    @Test
    void shouldReturn404_whenDeletingUnknownEmployee() throws Exception {
        doThrow(new ResourceNotFoundException("Employé", 9L)).when(employeeDeleter).delete(9L);

        mockMvc.perform(delete("/employees/{id}", 9L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn422_whenEmployeeHasHistory() throws Exception {
        doThrow(new BusinessException("historique")).when(employeeDeleter).delete(5L);

        mockMvc.perform(delete("/employees/{id}", 5L))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
