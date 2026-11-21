package com.hackaton.crm.interaction.repository;

import com.hackaton.crm.interaction.entity.Interaction;
import com.hackaton.crm.party.PartyType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    List<Interaction> findByPartyTypeAndPartyIdOrderByOccurredAtDesc(PartyType partyType, Long partyId);
}
