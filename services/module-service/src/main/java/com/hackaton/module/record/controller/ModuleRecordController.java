package com.hackaton.module.record.controller;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.dto.RecordPage;
import com.hackaton.module.record.dto.RecordQuery;
import com.hackaton.module.record.dto.RecordRequest;
import com.hackaton.module.record.service.creator.ModuleRecordCreator;
import com.hackaton.module.record.service.reader.ModuleRecordReader;
import com.hackaton.module.record.service.updater.ModuleRecordUpdater;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/definitions/{slug}/records")
@RequiredArgsConstructor
public class ModuleRecordController {

    static final String TOTAL_COUNT = "X-Total-Count";

    private final ModuleRecordReader recordReader;
    private final ModuleRecordCreator recordCreator;
    private final ModuleRecordUpdater recordUpdater;

    @GetMapping
    public ResponseEntity<List<ModuleRecordResponse>> search(
            @PathVariable String slug,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        RecordPage result = recordReader.search(slug, new RecordQuery(search, status, page, size));
        return ResponseEntity.ok().header(TOTAL_COUNT, String.valueOf(result.total())).body(result.items());
    }

    @PostMapping
    public ResponseEntity<ModuleRecordResponse> create(@PathVariable String slug, Caller caller,
            @Valid @RequestBody RecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordCreator.create(slug, request, caller));
    }

    @PostMapping("/{id}/advance")
    public ModuleRecordResponse advance(@PathVariable String slug, @PathVariable Long id) {
        return recordUpdater.advance(slug, id);
    }
}
