package com.hackaton.module.definition.service.creator;

import com.hackaton.module.common.domain.BuiltInModules;
import com.hackaton.module.common.exception.ConflictException;
import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.mapper.ModuleDefinitionMapper;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.rules.DefinitionRules;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModuleDefinitionCreatorService implements ModuleDefinitionCreator {

    private final ModuleDefinitionRepository definitionRepository;
    private final DefinitionRules definitionRules;
    private final SlugGenerator slugGenerator;
    private final ModuleDefinitionMapper definitionMapper;
    private final Clock clock;

    @Override
    @Transactional
    public ModuleDefinitionResponse create(ModuleDefinitionRequest request, Caller caller) {
        caller.requireAdmin();
        definitionRules.check(request);
        String slug = slugGenerator.fromName(request.name());
        if (slug.isEmpty()) {
            throw new InvalidInputException("Le nom du module doit contenir au moins une lettre ou un chiffre");
        }
        if (BuiltInModules.KEYS.contains(slug) || definitionRepository.existsBySlug(slug)) {
            throw new ConflictException("Un module porte déjà ce nom : " + request.name().trim());
        }
        var definition = definitionMapper.toEntity(request, slug, caller.author(), Instant.now(clock));
        return definitionMapper.toResponse(definitionRepository.save(definition));
    }
}
