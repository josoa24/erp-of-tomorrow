package com.hackaton.crm.interaction.repository;

import com.hackaton.crm.party.PartyType;
import java.time.Instant;

public record LastInteraction(PartyType partyType, Long partyId, Instant occurredAt) {
}
