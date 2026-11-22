package com.hackaton.module.salesimport.service;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.salesimport.dto.SalesImportAvailability;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesImportReaderService implements SalesImportReader {

    private final DefinitionLookup definitionLookup;
    private final SaleCandidateFinder candidateFinder;

    @Override
    public SalesImportAvailability availability(String slug) {
        ModuleDefinition definition = definitionLookup.require(slug);
        return new SalesImportAvailability(candidateFinder.candidates(definition, ImportColumns.of(definition)).size());
    }
}
