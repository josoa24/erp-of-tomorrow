package com.hackaton.crm.interaction.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hackaton.crm.interaction.entity.Interaction;
import com.hackaton.crm.interaction.entity.InteractionType;
import com.hackaton.crm.party.PartyType;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class InteractionRepositoryTest {

    @Autowired
    private InteractionRepository repository;

    private void save(PartyType type, Long partyId, String occurredAt) {
        Interaction interaction = new Interaction();
        interaction.setPartyType(type);
        interaction.setPartyId(partyId);
        interaction.setType(InteractionType.APPEL);
        interaction.setOccurredAt(Instant.parse(occurredAt));
        interaction.setSummary("Relance du devis");
        interaction.setAuthor("crm@erp.local");
        repository.save(interaction);
    }

    @Test
    void shouldReturnTheLatestExchangePerParty_whenPartiesHaveSeveralExchanges() {
        save(PartyType.CUSTOMER, 1L, "2026-11-01T07:00:00Z");
        save(PartyType.CUSTOMER, 1L, "2026-11-20T07:00:00Z");
        save(PartyType.CUSTOMER, 2L, "2026-10-05T07:00:00Z");
        save(PartyType.SUPPLIER, 1L, "2026-09-12T07:00:00Z");

        assertThat(repository.findLastInteractions()).containsExactlyInAnyOrder(
                new LastInteraction(PartyType.CUSTOMER, 1L, Instant.parse("2026-11-20T07:00:00Z")),
                new LastInteraction(PartyType.CUSTOMER, 2L, Instant.parse("2026-10-05T07:00:00Z")),
                new LastInteraction(PartyType.SUPPLIER, 1L, Instant.parse("2026-09-12T07:00:00Z")));
    }

    @Test
    void shouldReturnNothing_whenNoExchangeIsRecorded() {
        assertThat(repository.findLastInteractions()).isEmpty();
    }
}
