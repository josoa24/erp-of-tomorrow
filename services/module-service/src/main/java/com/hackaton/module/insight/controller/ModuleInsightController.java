package com.hackaton.module.insight.controller;

import com.hackaton.module.insight.dto.IndicatorValueResponse;
import com.hackaton.module.insight.dto.StatusCountsResponse;
import com.hackaton.module.insight.service.IndicatorReader;
import com.hackaton.module.insight.service.StatusCountReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/definitions/{slug}")
@RequiredArgsConstructor
public class ModuleInsightController {

    private final IndicatorReader indicatorReader;
    private final StatusCountReader statusCountReader;

    @GetMapping("/indicators")
    public List<IndicatorValueResponse> indicators(@PathVariable String slug) {
        return indicatorReader.compute(slug);
    }

    @GetMapping("/status-counts")
    public StatusCountsResponse statusCounts(@PathVariable String slug) {
        return statusCountReader.count(slug);
    }
}
