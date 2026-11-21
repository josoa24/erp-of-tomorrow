package com.hackaton.rh.department.controller;

import static com.hackaton.rh.common.GatewayRequests.delete;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.department.service.creator.DepartmentCreator;
import com.hackaton.rh.department.service.deleter.DepartmentDeleter;
import com.hackaton.rh.department.service.reader.DepartmentReader;
import com.hackaton.rh.department.service.updater.DepartmentUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerDeleteTest {

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

    @Test
    void shouldReturn204AndDelete_whenDepartmentIsEmpty() throws Exception {
        mockMvc.perform(delete("/departments/{id}", 3L))
                .andExpect(status().isNoContent());

        verify(departmentDeleter).delete(3L);
    }

    @Test
    void shouldReturn404_whenDeletingUnknownDepartment() throws Exception {
        doThrow(new ResourceNotFoundException("Département", 9L)).when(departmentDeleter).delete(9L);

        mockMvc.perform(delete("/departments/{id}", 9L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn422_whenDepartmentHasEmployees() throws Exception {
        doThrow(new BusinessException("a des employés")).when(departmentDeleter).delete(3L);

        mockMvc.perform(delete("/departments/{id}", 3L))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
