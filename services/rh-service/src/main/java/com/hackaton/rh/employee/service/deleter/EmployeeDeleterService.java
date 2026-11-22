package com.hackaton.rh.employee.service.deleter;

import com.hackaton.rh.advance.repository.SalaryAdvanceRepository;
import com.hackaton.rh.attendance.repository.AttendanceRepository;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.employee.repository.EmployeeRepository;
import com.hackaton.rh.leave.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeDeleterService implements EmployeeDeleter {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryAdvanceRepository advanceRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employé", id);
        }
        if (leaveRepository.existsByEmployeeId(id) || attendanceRepository.existsByEmployeeId(id)
                || advanceRepository.existsByEmployeeId(id)) {
            throw new BusinessException(
                    "Cet employé a un historique (congés, pointages ou avances) et ne peut pas être supprimé");
        }
        employeeRepository.deleteById(id);
    }
}
