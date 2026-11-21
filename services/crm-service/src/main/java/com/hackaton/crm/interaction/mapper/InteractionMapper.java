package com.hackaton.crm.interaction.mapper;

import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.interaction.entity.Interaction;
import org.springframework.stereotype.Component;

@Component
public class InteractionMapper {

    public InteractionResponse toResponse(Interaction interaction) {
        return new InteractionResponse(
                interaction.getId(),
                interaction.getPartyType(),
                interaction.getPartyId(),
                interaction.getType(),
                interaction.getOccurredAt(),
                interaction.getSummary(),
                interaction.getAuthor(),
                interaction.getCreatedAt());
    }
}
