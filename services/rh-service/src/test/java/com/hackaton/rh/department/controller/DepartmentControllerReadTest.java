package com.hackaton.rh.department.controller;

import static com.hackaton.rh.common.GatewayRequests.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.dto.DepartmentResponse;
import com.hackaton.rh.department.service.creator.DepartmentCreator;
import com.hackaton.rh.department.service.deleter.DepartmentDeleter;
import com.hackaton.rh.department.service.reader.DepartmentReader;
import com.hackaton.rh.department.service.updater.DepartmentUpdater;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerReadTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DepartmentReader departmentReader;
    @MockitoBean
    private DepartmentCreator departmentCreator;
    @MockitoBean
    private DepartmentUpdater departmentUpdater;
    @MockitoBean
    private DepartmentDeleter departmentDeleter;

    private final DepartmentResponse finance = new DepartmentResponse(3L, "Finance", Instant.EPOCH);

    @Test
    void shouldReturn200WithDepartments_whenListing() throws Exception {
        when(departmentReader.findAll()).thenReturn(List.of(finance));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Finance"));
    }

    @Test
    void shouldReturn200WithDepartment_whenDepartmentExists() throws Exception {
        when(departmentReader.findById(3L)).thenReturn(finance);

        mockMvc.perform(get("/departments/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Finance"));
    }

    @Test
    void shouldReturn404_whenDepartmentDoesNotExist() throws Exception {
        when(departmentReader.findById(9L)).thenThrow(new ResourceNotFoundException("Département", 9L));

        mockMvc.perform(get("/departments/{id}", 9L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn400_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/departments/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
