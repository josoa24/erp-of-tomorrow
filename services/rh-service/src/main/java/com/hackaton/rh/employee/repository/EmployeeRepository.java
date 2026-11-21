package com.hackaton.rh.employee.repository;

import com.hackaton.rh.employee.entity.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByDepartmentId(Long departmentId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Employee> findAllByOrderByLastNameAscFirstNameAsc();
}
