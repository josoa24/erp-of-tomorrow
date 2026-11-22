package com.hackaton.rh.employee.service.link;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeAccountLinkCheckerTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeAccountLinkChecker checker;

    @Test
    void shouldAccept_whenNoAccountIsLinked() {
        assertDoesNotThrow(() -> checker.checkAvailable(null, null));
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void shouldAccept_whenAccountIsFreeOnCreation() {
        when(employeeRepository.existsByUserId(4L)).thenReturn(false);

        assertDoesNotThrow(() -> checker.checkAvailable(4L, null));
    }

    @Test
    void shouldRefuse_whenAccountIsLinkedToAnotherEmployeeOnCreation() {
        when(employeeRepository.existsByUserId(4L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> checker.checkAvailable(4L, null));
    }

    @Test
    void shouldIgnoreTheEmployeeItself_whenUpdating() {
        when(employeeRepository.existsByUserIdAndIdNot(4L, 7L)).thenReturn(false);

        assertDoesNotThrow(() -> checker.checkAvailable(4L, 7L));
    }

    @Test
    void shouldRefuse_whenAccountIsLinkedToAnotherEmployeeOnUpdate() {
        when(employeeRepository.existsByUserIdAndIdNot(4L, 7L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> checker.checkAvailable(4L, 7L));
    }
}
