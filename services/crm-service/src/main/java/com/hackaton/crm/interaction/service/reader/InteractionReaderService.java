package com.hackaton.crm.interaction.service.reader;

import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.interaction.mapper.InteractionMapper;
import com.hackaton.crm.interaction.repository.InteractionRepository;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.service.PartyVerifier;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InteractionReaderService implements InteractionReader {

    private final InteractionRepository interactionRepository;
    private final PartyVerifier partyVerifier;
    private final InteractionMapper interactionMapper;

    @Override
    public List<InteractionResponse> findByParty(PartyType partyType, Long partyId) {
        partyVerifier.verify(partyType, partyId);
        return interactionRepository.findByPartyTypeAndPartyIdOrderByOccurredAtDesc(partyType, partyId).stream()
                .map(interactionMapper::toResponse)
                .toList();
    }
}
