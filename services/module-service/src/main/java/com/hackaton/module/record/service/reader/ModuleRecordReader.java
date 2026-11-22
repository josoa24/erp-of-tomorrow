package com.hackaton.module.record.service.reader;

import com.hackaton.module.record.dto.RecordPage;
import com.hackaton.module.record.dto.RecordQuery;

public interface ModuleRecordReader {

    RecordPage search(String slug, RecordQuery query);
}
