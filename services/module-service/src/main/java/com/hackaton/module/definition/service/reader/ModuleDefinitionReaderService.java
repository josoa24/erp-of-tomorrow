package com.hackaton.module.definition.service.reader;

import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.dto.ModuleSummary;
import com.hackaton.module.definition.mapper.ModuleDefinitionMapper;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleDefinitionReaderService implements ModuleDefinitionReader {

    private final ModuleDefinitionRepository definitionRepository;
    private final DefinitionLookup definitionLookup;
    private final ModuleDefinitionMapper definitionMapper;

    @Override
    public List<ModuleSummary> findAll() {
        return definitionRepository.findAllByOrderByCreatedAtAscIdAsc().stream()
                .map(definitionMapper::toSummary)
                .toList();
    }

    @Override
    public ModuleDefinitionResponse findBySlug(String slug) {
        return definitionMapper.toResponse(definitionLookup.require(slug));
    }
}
