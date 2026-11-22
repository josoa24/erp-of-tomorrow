package com.hackaton.module.adaptation.service.deleter;

import com.hackaton.module.adaptation.entity.AdaptationActivation;
import com.hackaton.module.adaptation.repository.AdaptationActivationRepository;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.common.security.Caller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdaptationDeleterService implements AdaptationDeleter {

    private final AdaptationActivationRepository activationRepository;

    @Override
    @Transactional
    public void deactivate(String adaptationKey, Caller caller) {
        caller.requireAdmin();
        AdaptationActivation activation = activationRepository.findByAdaptationKey(adaptationKey)
                .orElseThrow(() -> new ResourceNotFoundException("Adaptation non active : " + adaptationKey));
        activationRepository.delete(activation);
    }
}
