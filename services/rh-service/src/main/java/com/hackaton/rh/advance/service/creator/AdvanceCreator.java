package com.hackaton.rh.advance.service.creator;

import com.hackaton.rh.advance.dto.AdvanceCreateRequest;
import com.hackaton.rh.advance.dto.AdvanceResponse;

public interface AdvanceCreator {

    AdvanceResponse create(AdvanceCreateRequest request);
}
