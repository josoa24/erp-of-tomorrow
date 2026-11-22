package com.hackaton.module.insight.service;

import com.hackaton.module.insight.dto.StatusCountsResponse;

public interface StatusCountReader {

    StatusCountsResponse count(String slug);
}
