package com.hackaton.crm.interaction.service.creator;

import com.hackaton.crm.interaction.dto.InteractionRequest;
import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.interaction.entity.Interaction;
import com.hackaton.crm.interaction.mapper.InteractionMapper;
import com.hackaton.crm.interaction.repository.InteractionRepository;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.service.PartyVerifier;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InteractionCreatorService implements InteractionCreator {

    public static final String UNKNOWN_AUTHOR = "inconnu";

    private final InteractionRepository interactionRepository;
    private final PartyVerifier partyVerifier;
    private final InteractionMapper interactionMapper;

    @Override
    public InteractionResponse create(PartyType partyType, Long partyId, InteractionRequest request, String author) {
        partyVerifier.verify(partyType, partyId);
        Interaction interaction = new Interaction();
        interaction.setPartyType(partyType);
        interaction.setPartyId(partyId);
        interaction.setType(request.type());
        interaction.setOccurredAt(request.occurredAt() == null ? Instant.now() : request.occurredAt());
        interaction.setSummary(request.summary().trim());
        interaction.setAuthor(author == null || author.isBlank() ? UNKNOWN_AUTHOR : author.trim());
        return interactionMapper.toResponse(interactionRepository.save(interaction));
    }
}
