package com.hackaton.rh.employee.controller;

import static com.hackaton.rh.common.GatewayRequests.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.dto.EmployeeResponse;
import com.hackaton.rh.employee.service.creator.EmployeeCreator;
import com.hackaton.rh.employee.service.deleter.EmployeeDeleter;
import com.hackaton.rh.employee.service.reader.EmployeeReader;
import com.hackaton.rh.employee.service.updater.EmployeeUpdater;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerReadTest {

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
    void shouldReturn200WithFilteredEmployees_whenSearching() throws Exception {
        EmployeeResponse jean = EmployeeFixtures.jean();
        when(employeeReader.search("dupont", 3L)).thenReturn(List.of(jean));

        mockMvc.perform(get("/employees").param("search", "dupont").param("departmentId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("jean.dupont@erp.local"));
    }

    @Test
    void shouldReturn200WithLeaveBalance_whenEmployeeExists() throws Exception {
        EmployeeResponse jean = EmployeeFixtures.jean();
        when(employeeReader.findById(5L)).thenReturn(jean);

        mockMvc.perform(get("/employees/{id}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.leaveBalance").value(25));
    }

    @Test
    void shouldReturn404_whenEmployeeDoesNotExist() throws Exception {
        when(employeeReader.findById(9L)).thenThrow(new ResourceNotFoundException("Employé", 9L));

        mockMvc.perform(get("/employees/{id}", 9L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
