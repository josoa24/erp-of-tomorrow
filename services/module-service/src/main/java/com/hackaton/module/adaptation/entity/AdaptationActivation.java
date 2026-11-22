package com.hackaton.module.adaptation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "adaptation_activation", uniqueConstraints = @UniqueConstraint(
        name = "uk_adaptation_activation_key", columnNames = "adaptation_key"))
@Getter
@Setter
@NoArgsConstructor
public class AdaptationActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_key", nullable = false, length = 60)
    private String moduleKey;

    @Column(name = "adaptation_key", nullable = false, length = 80)
    private String adaptationKey;

    @Column(name = "activated_by", nullable = false)
    private String activatedBy;

    @Column(name = "activated_at", nullable = false, updatable = false)
    private Instant activatedAt;
}
