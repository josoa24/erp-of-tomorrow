package com.hackaton.module.record.service.values;

import com.hackaton.module.definition.entity.ModuleDefinition;
import java.util.Map;

public interface RecordValuesValidator {

    Map<String, Object> validate(ModuleDefinition definition, Map<String, Object> raw);
}
