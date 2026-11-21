package com.hackaton.rh.employee.service.link;

import com.hackaton.rh.common.exception.DuplicateResourceException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeAccountLinkChecker {

    private final EmployeeRepository employeeRepository;

    public void checkAvailable(Long userId, Long employeeId) {
        if (userId == null) {
            return;
        }
        boolean taken = employeeId == null
                ? employeeRepository.existsByUserId(userId)
                : employeeRepository.existsByUserIdAndIdNot(userId, employeeId);
        if (taken) {
            throw new DuplicateResourceException("Le compte " + userId + " est déjà lié à un autre employé");
        }
    }
}
