package com.hackaton.rh.advance.repository;

import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalaryAdvanceRepository extends JpaRepository<SalaryAdvance, Long> {

    boolean existsByEmployeeId(Long employeeId);

    List<SalaryAdvance> findAllByOrderByCreatedAtDesc();

    List<SalaryAdvance> findAllByMonthAndStatusIn(LocalDate month, Collection<AdvanceStatus> statuses);

    @Query("select coalesce(sum(a.amount), 0) from SalaryAdvance a where a.employeeId = :employeeId "
            + "and a.month = :month and a.status in :statuses")
    BigDecimal sumAmount(
            @Param("employeeId") Long employeeId,
            @Param("month") LocalDate month,
            @Param("statuses") Collection<AdvanceStatus> statuses);
}
