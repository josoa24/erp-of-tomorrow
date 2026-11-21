package com.hackaton.rh.attendance.repository;

import com.hackaton.rh.attendance.entity.Attendance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByEmployeeId(Long employeeId);

    Optional<Attendance> findFirstByEmployeeIdAndCheckOutIsNullOrderByCheckInDesc(Long employeeId);

    List<Attendance> findAllByOrderByCheckInDesc();
}
