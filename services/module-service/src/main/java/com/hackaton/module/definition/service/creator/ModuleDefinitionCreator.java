package com.hackaton.module.definition.service.creator;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.ModuleDefinitionResponse;

public interface ModuleDefinitionCreator {

    ModuleDefinitionResponse create(ModuleDefinitionRequest request, Caller caller);
}
