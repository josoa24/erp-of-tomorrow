package com.hackaton.module.adaptation.service.creator;

import com.hackaton.module.adaptation.catalog.AdaptationCatalog;
import com.hackaton.module.adaptation.dto.AdaptationRequest;
import com.hackaton.module.adaptation.dto.AdaptationResponse;
import com.hackaton.module.adaptation.entity.AdaptationActivation;
import com.hackaton.module.adaptation.mapper.AdaptationMapper;
import com.hackaton.module.adaptation.repository.AdaptationActivationRepository;
import com.hackaton.module.common.exception.ConflictException;
import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.common.security.Caller;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdaptationCreatorService implements AdaptationCreator {

    private final AdaptationActivationRepository activationRepository;
    private final AdaptationCatalog catalog;
    private final AdaptationMapper adaptationMapper;
    private final Clock clock;

    @Override
    @Transactional
    public AdaptationResponse activate(AdaptationRequest request, Caller caller) {
        caller.requireAdmin();
        String moduleKey = catalog.moduleOf(request.adaptationKey())
                .orElseThrow(() -> new InvalidInputException("Adaptation inconnue : " + request.adaptationKey()));
        if (!moduleKey.equals(request.moduleKey())) {
            throw new InvalidInputException("L'adaptation " + request.adaptationKey()
                    + " appartient au module " + moduleKey);
        }
        if (activationRepository.existsByAdaptationKey(request.adaptationKey())) {
            throw new ConflictException("L'adaptation " + request.adaptationKey() + " est déjà active");
        }
        AdaptationActivation activation = new AdaptationActivation();
        activation.setModuleKey(moduleKey);
        activation.setAdaptationKey(request.adaptationKey());
        activation.setActivatedBy(caller.author());
        activation.setActivatedAt(Instant.now(clock));
        return adaptationMapper.toResponse(activationRepository.save(activation));
    }
}
