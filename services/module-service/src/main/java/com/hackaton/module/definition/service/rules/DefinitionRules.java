package com.hackaton.module.definition.service.rules;

import com.hackaton.module.definition.dto.ModuleDefinitionRequest;

public interface DefinitionRules {

    void check(ModuleDefinitionRequest request);
}
