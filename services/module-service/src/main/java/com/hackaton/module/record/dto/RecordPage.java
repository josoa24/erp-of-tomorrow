package com.hackaton.module.record.dto;

import java.util.List;

public record RecordPage(List<ModuleRecordResponse> items, long total) {
}
