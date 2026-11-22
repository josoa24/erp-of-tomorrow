package com.hackaton.rh.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    public static final int INITIAL_LEAVE_BALANCE = 25;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    private String position;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private ContractType contractType;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal baseSalary;

    @Column(unique = true)
    private Long userId;

    @Column(nullable = false)
    private int leaveBalance = INITIAL_LEAVE_BALANCE;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public String fullName() {
        return firstName + " " + lastName;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
