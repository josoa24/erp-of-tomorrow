package com.hackaton.module.adaptation.service.reader;

import com.hackaton.module.adaptation.dto.AdaptationResponse;
import com.hackaton.module.adaptation.mapper.AdaptationMapper;
import com.hackaton.module.adaptation.repository.AdaptationActivationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdaptationReaderService implements AdaptationReader {

    private final AdaptationActivationRepository activationRepository;
    private final AdaptationMapper adaptationMapper;

    @Override
    public List<AdaptationResponse> findAll() {
        return activationRepository.findAllByOrderByActivatedAtAscIdAsc().stream()
                .map(adaptationMapper::toResponse)
                .toList();
    }
}
