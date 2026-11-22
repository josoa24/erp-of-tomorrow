package com.hackaton.module.reference.service;

import com.hackaton.module.definition.entity.ReferenceTarget;

public interface ReferenceResolver {

    ReferenceValue resolve(ReferenceTarget target, Long id);
}
