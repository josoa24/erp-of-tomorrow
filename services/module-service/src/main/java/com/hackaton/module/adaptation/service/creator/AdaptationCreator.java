package com.hackaton.module.adaptation.service.creator;

import com.hackaton.module.adaptation.dto.AdaptationRequest;
import com.hackaton.module.adaptation.dto.AdaptationResponse;
import com.hackaton.module.common.security.Caller;

public interface AdaptationCreator {

    AdaptationResponse activate(AdaptationRequest request, Caller caller);
}
