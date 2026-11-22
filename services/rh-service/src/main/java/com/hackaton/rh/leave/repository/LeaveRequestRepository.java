package com.hackaton.rh.leave.repository;

import com.hackaton.rh.leave.entity.LeaveRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    boolean existsByEmployeeId(Long employeeId);

    List<LeaveRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(LeaveStatus status);

    @Query("select count(l) > 0 from LeaveRequest l where l.employeeId = :employeeId "
            + "and l.status in :statuses and l.startDate <= :end and l.endDate >= :start")
    boolean existsOverlap(
            @Param("employeeId") Long employeeId,
            @Param("statuses") List<LeaveStatus> statuses,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("select count(l) from LeaveRequest l where l.status = :status "
            + "and l.startDate <= :day and l.endDate >= :day")
    long countActiveOn(@Param("status") LeaveStatus status, @Param("day") LocalDate day);
}
