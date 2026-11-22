package com.hackaton.module.definition.service.deleter;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModuleDefinitionDeleterService implements ModuleDefinitionDeleter {

    private final DefinitionLookup definitionLookup;
    private final ModuleDefinitionRepository definitionRepository;
    private final ModuleRecordRepository recordRepository;

    @Override
    @Transactional
    public void delete(String slug, Caller caller) {
        caller.requireAdmin();
        ModuleDefinition definition = definitionLookup.require(slug);
        if (recordRepository.existsByDefinitionId(definition.getId())) {
            throw new BusinessException("Le module " + definition.getName()
                    + " contient des enregistrements : il ne peut pas être supprimé");
        }
        definitionRepository.delete(definition);
    }
}
