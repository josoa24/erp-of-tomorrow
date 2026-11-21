package com.hackaton.crm.interaction.service.reader;

import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.party.PartyType;
import java.util.List;

public interface InteractionReader {

    List<InteractionResponse> findByParty(PartyType partyType, Long partyId);
}
