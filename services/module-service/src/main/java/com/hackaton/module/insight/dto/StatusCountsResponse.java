package com.hackaton.module.insight.dto;

import java.util.Map;

public record StatusCountsResponse(long all, Map<String, Long> byStatus) {
}
