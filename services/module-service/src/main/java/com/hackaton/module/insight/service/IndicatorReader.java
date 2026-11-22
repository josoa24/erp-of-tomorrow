package com.hackaton.module.insight.service;

import com.hackaton.module.insight.dto.IndicatorValueResponse;
import java.util.List;

public interface IndicatorReader {

    List<IndicatorValueResponse> compute(String slug);
}
