package com.hackaton.rh.department.controller;

import static com.hackaton.rh.common.GatewayRequests.postJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.department.dto.DepartmentRequest;
import com.hackaton.rh.department.dto.DepartmentResponse;
import com.hackaton.rh.department.service.creator.DepartmentCreator;
import com.hackaton.rh.department.service.deleter.DepartmentDeleter;
import com.hackaton.rh.department.service.reader.DepartmentReader;
import com.hackaton.rh.department.service.updater.DepartmentUpdater;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerCreateTest {

    private static final String VALID_BODY = "{\"name\":\"Finance\"}";
    private static final String BLANK_NAME_BODY = "{\"name\":\"  \"}";

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

    private final DepartmentRequest request = new DepartmentRequest("Finance");

    @Test
    void shouldReturn201WithDepartment_whenCreatingValidDepartment() throws Exception {
        when(departmentCreator.create(request)).thenReturn(new DepartmentResponse(3L, "Finance", Instant.EPOCH));

        mockMvc.perform(postJson("/departments", VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Finance"));
    }

    @Test
    void shouldReturn400_whenCreatingDepartmentWithBlankName() throws Exception {
        mockMvc.perform(postJson("/departments", BLANK_NAME_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn409_whenCreatingDuplicateDepartment() throws Exception {
        when(departmentCreator.create(request)).thenThrow(new DuplicateResourceException("existe déjà"));

        mockMvc.perform(postJson("/departments", VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
