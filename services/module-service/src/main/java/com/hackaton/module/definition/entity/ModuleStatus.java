package com.hackaton.module.definition.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "module_status", uniqueConstraints = @UniqueConstraint(
        name = "uk_module_status_definition_key", columnNames = {"definition_id", "`key`"}))
@Getter
@Setter
@NoArgsConstructor
public class ModuleStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definition_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_module_status_definition"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ModuleDefinition definition;

    @Column(nullable = false)
    private int position;

    @Column(name = "`key`", nullable = false, length = 40)
    private String key;

    @Column(nullable = false, length = 60)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTone tone;

    @Column(name = "`final`", nullable = false)
    private boolean finalStatus;
}
