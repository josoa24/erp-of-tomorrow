package com.hackaton.module.definition.repository;

import com.hackaton.module.definition.entity.ModuleDefinition;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleDefinitionRepository extends JpaRepository<ModuleDefinition, Long> {

    Optional<ModuleDefinition> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<ModuleDefinition> findAllByOrderByCreatedAtAscIdAsc();
}
