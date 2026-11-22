package com.hackaton.module.definition.dto;

import java.time.Instant;

public record ModuleSummary(Long id, String slug, String name, String icon, Instant createdAt) {
}
