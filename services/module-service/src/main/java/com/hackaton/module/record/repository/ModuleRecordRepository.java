package com.hackaton.module.record.repository;

import com.hackaton.module.record.entity.ModuleRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRecordRepository extends JpaRepository<ModuleRecord, Long> {

    List<ModuleRecord> findByDefinitionIdOrderByCreatedAtDescIdDesc(Long definitionId);

    Optional<ModuleRecord> findByIdAndDefinitionId(Long id, Long definitionId);

    boolean existsByDefinitionId(Long definitionId);
}
