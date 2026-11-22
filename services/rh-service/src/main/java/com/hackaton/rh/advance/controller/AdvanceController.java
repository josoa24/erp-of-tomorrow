package com.hackaton.rh.advance.controller;

import com.hackaton.rh.advance.dto.AdvanceCreateRequest;
import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.dto.AdvanceSummaryResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.service.creator.AdvanceCreator;
import com.hackaton.rh.advance.service.reader.AdvanceReader;
import com.hackaton.rh.advance.service.summary.AdvanceSummaryReader;
import jakarta.validation.Valid;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advances")
@RequiredArgsConstructor
public class AdvanceController {

    private final AdvanceReader advanceReader;
    private final AdvanceSummaryReader summaryReader;
    private final AdvanceCreator advanceCreator;

    @GetMapping
    public List<AdvanceResponse> search(
            @RequestParam(required = false) YearMonth month,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) AdvanceStatus status) {
        return advanceReader.search(month, employeeId, status);
    }

    @GetMapping("/summary")
    public AdvanceSummaryResponse summarize() {
        return summaryReader.summarize();
    }

    @PostMapping
    public ResponseEntity<AdvanceResponse> create(@Valid @RequestBody AdvanceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(advanceCreator.create(request));
    }
}
