package com.hackaton.module.record.service.updater;

import com.hackaton.module.record.dto.ModuleRecordResponse;

public interface ModuleRecordUpdater {

    ModuleRecordResponse advance(String slug, Long recordId);
}
