package com.hackaton.module.record.entity;

import com.hackaton.module.definition.entity.ModuleDefinition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "module_record",
        indexes = @Index(name = "idx_module_record_definition_status", columnList = "definition_id, status"))
@Getter
@Setter
@NoArgsConstructor
public class ModuleRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definition_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_module_record_definition"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private ModuleDefinition definition;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "`values`", nullable = false)
    private Map<String, Object> values = new LinkedHashMap<>();

    @Column(nullable = false, length = 40)
    private String status;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
