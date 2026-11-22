package com.hackaton.module.record.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record RecordRequest(@NotNull Map<String, Object> values) {
}
