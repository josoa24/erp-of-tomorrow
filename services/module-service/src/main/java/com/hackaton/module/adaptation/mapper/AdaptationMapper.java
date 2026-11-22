package com.hackaton.module.adaptation.mapper;

import com.hackaton.module.adaptation.dto.AdaptationResponse;
import com.hackaton.module.adaptation.entity.AdaptationActivation;
import org.springframework.stereotype.Component;

@Component
public class AdaptationMapper {

    public AdaptationResponse toResponse(AdaptationActivation activation) {
        return new AdaptationResponse(activation.getModuleKey(), activation.getAdaptationKey(),
                activation.getActivatedBy(), activation.getActivatedAt());
    }
}
