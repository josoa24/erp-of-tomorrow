package com.hackaton.rh.employee.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hackaton.rh.employee.dto.EmployeeResponse;

/**
 * Données de test des controllers employés, indépendantes des constructeurs des DTO.
 */
final class EmployeeFixtures {

    static final String VALID_BODY = """
            {"firstName":"Jean","lastName":"Dupont","email":"jean.dupont@erp.local","position":"Comptable",
             "departmentId":3,"hireDate":"2025-01-15","baseSalary":2500.00}
            """;
    static final String INVALID_EMAIL_BODY = VALID_BODY.replace("jean.dupont@erp.local", "pas-un-email");

    private EmployeeFixtures() {
    }

    static EmployeeResponse jean() {
        EmployeeResponse response = mock(EmployeeResponse.class);
        when(response.id()).thenReturn(5L);
        when(response.firstName()).thenReturn("Jean");
        when(response.email()).thenReturn("jean.dupont@erp.local");
        when(response.leaveBalance()).thenReturn(25);
        return response;
    }
}
