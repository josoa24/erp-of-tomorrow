package com.hackaton.module.definition.service.reader;

import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.dto.ModuleSummary;
import java.util.List;

public interface ModuleDefinitionReader {

    List<ModuleSummary> findAll();

    ModuleDefinitionResponse findBySlug(String slug);
}
