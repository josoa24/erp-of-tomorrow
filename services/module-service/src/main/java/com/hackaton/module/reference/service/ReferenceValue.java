package com.hackaton.module.reference.service;

import java.util.LinkedHashMap;
import java.util.Map;

public record ReferenceValue(Long id, String label) {

    public Map<String, Object> toMap() {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", id);
        value.put("label", label);
        return value;
    }
}
