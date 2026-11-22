package com.hackaton.module.definition.service.reader;

import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefinitionLookup {

    private final ModuleDefinitionRepository definitionRepository;

    public ModuleDefinition require(String slug) {
        return definitionRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Module introuvable : " + slug));
    }
}
