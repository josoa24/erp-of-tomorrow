package com.hackaton.crm.interaction.service.creator;

import com.hackaton.crm.interaction.dto.InteractionRequest;
import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.party.PartyType;

public interface InteractionCreator {

    InteractionResponse create(PartyType partyType, Long partyId, InteractionRequest request, String author);
}
