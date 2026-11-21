package com.hackaton.rh.summary.controller;

import com.hackaton.rh.summary.dto.HrSummaryResponse;
import com.hackaton.rh.summary.service.HrSummaryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor
public class HrSummaryController {

    private final HrSummaryReader summaryReader;

    @GetMapping
    public HrSummaryResponse summarize() {
        return summaryReader.summarize();
    }
}
