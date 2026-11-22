package com.hackaton.module.record.service.creator;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.dto.RecordRequest;

public interface ModuleRecordCreator {

    ModuleRecordResponse create(String slug, RecordRequest request, Caller caller);
}
