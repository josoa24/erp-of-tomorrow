package com.hackaton.rh.advance.entity;

import com.hackaton.rh.employee.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "salary_advances",
        indexes = @Index(name = "idx_salary_advances_employee_month", columnList = "employee_id, month"))
@Check(name = "salary_advances_amount_check", constraints = "amount > 0")
@Getter
@Setter
@NoArgsConstructor
public class SalaryAdvance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false, updatable = false)
    private Long employeeId;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_salary_advances_employee"))
    private Employee employee;

    @Column(nullable = false, updatable = false)
    private LocalDate month;

    @Column(nullable = false, updatable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(length = 500, updatable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AdvanceStatus status = AdvanceStatus.DRAFT;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant decidedAt;

    private Long decidedBy;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
