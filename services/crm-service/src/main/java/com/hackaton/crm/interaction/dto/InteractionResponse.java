package com.hackaton.crm.interaction.dto;

import com.hackaton.crm.interaction.entity.InteractionType;
import com.hackaton.crm.party.PartyType;
import java.time.Instant;

public record InteractionResponse(
        Long id,
        PartyType partyType,
        Long partyId,
        InteractionType type,
        Instant occurredAt,
        String summary,
        String author,
        Instant createdAt) {
}
