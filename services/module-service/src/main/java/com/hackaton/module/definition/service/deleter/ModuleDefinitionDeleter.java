package com.hackaton.module.definition.service.deleter;

import com.hackaton.module.common.security.Caller;

public interface ModuleDefinitionDeleter {

    void delete(String slug, Caller caller);
}
