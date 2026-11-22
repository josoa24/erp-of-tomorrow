package com.hackaton.crm.interaction.repository;

import com.hackaton.crm.interaction.entity.Interaction;
import com.hackaton.crm.party.PartyType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    List<Interaction> findByPartyTypeAndPartyIdOrderByOccurredAtDesc(PartyType partyType, Long partyId);

    @Query("select new com.hackaton.crm.interaction.repository.LastInteraction("
            + "i.partyType, i.partyId, max(i.occurredAt)) "
            + "from Interaction i group by i.partyType, i.partyId")
    List<LastInteraction> findLastInteractions();
}
