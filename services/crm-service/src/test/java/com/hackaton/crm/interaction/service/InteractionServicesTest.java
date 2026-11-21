package com.hackaton.crm.interaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.crm.common.exception.ResourceNotFoundException;
import com.hackaton.crm.interaction.dto.InteractionRequest;
import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.interaction.entity.Interaction;
import com.hackaton.crm.interaction.entity.InteractionType;
import com.hackaton.crm.interaction.mapper.InteractionMapper;
import com.hackaton.crm.interaction.repository.InteractionRepository;
import com.hackaton.crm.interaction.service.creator.InteractionCreatorService;
import com.hackaton.crm.interaction.service.reader.InteractionReaderService;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.PartyInfo;
import com.hackaton.crm.party.service.PartyVerifier;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InteractionServicesTest {

    @Mock
    private InteractionRepository interactionRepository;
    @Mock
    private PartyVerifier partyVerifier;

    private final InteractionMapper mapper = new InteractionMapper();
    private InteractionCreatorService creator;
    private InteractionReaderService reader;

    @BeforeEach
    void setUp() {
        creator = new InteractionCreatorService(interactionRepository, partyVerifier, mapper);
        reader = new InteractionReaderService(interactionRepository, partyVerifier, mapper);
    }

    @Test
    void shouldCreateInteraction_withAuthorTrimmedSummaryAndDefaultDate() {
        when(partyVerifier.verify(PartyType.CUSTOMER, 1L)).thenReturn(new PartyInfo(1L, "Epicerie"));
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(call -> call.getArgument(0));

        InteractionResponse response = creator.create(PartyType.CUSTOMER, 1L,
                new InteractionRequest(InteractionType.APPEL, null, "  Appel de suivi  "), "crm@erp.local");

        assertEquals("Appel de suivi", response.summary());
        assertEquals("crm@erp.local", response.author());
        assertEquals(InteractionType.APPEL, response.type());
        assertNotNull(response.occurredAt());
    }

    @Test
    void shouldUseUnknownAuthor_whenHeaderIsMissing() {
        when(partyVerifier.verify(PartyType.SUPPLIER, 2L)).thenReturn(new PartyInfo(2L, "Socolait"));
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(call -> call.getArgument(0));

        InteractionResponse response = creator.create(PartyType.SUPPLIER, 2L,
                new InteractionRequest(InteractionType.NOTE, Instant.parse("2026-11-01T09:00:00Z"), "Note"), null);

        assertEquals(InteractionCreatorService.UNKNOWN_AUTHOR, response.author());
        assertEquals(Instant.parse("2026-11-01T09:00:00Z"), response.occurredAt());
    }

    @Test
    void shouldNotSave_whenPartyDoesNotExist() {
        when(partyVerifier.verify(PartyType.CUSTOMER, 9L)).thenThrow(new ResourceNotFoundException("Client", 9L));

        assertThrows(ResourceNotFoundException.class, () -> creator.create(PartyType.CUSTOMER, 9L,
                new InteractionRequest(InteractionType.NOTE, null, "x"), "a@b.c"));
        verify(interactionRepository, never()).save(any());
    }

    @Test
    void shouldListInteractionsOfAPartyInRepositoryOrder() {
        Interaction recent = interaction(2L, "2026-11-20T10:00:00Z", "Recent");
        Interaction old = interaction(1L, "2026-11-01T10:00:00Z", "Ancien");
        when(partyVerifier.verify(PartyType.CUSTOMER, 1L)).thenReturn(new PartyInfo(1L, "Epicerie"));
        when(interactionRepository.findByPartyTypeAndPartyIdOrderByOccurredAtDesc(PartyType.CUSTOMER, 1L))
                .thenReturn(List.of(recent, old));

        List<InteractionResponse> result = reader.findByParty(PartyType.CUSTOMER, 1L);

        assertEquals(List.of("Recent", "Ancien"), result.stream().map(InteractionResponse::summary).toList());
    }

    @Test
    void shouldRefuseListing_whenPartyDoesNotExist() {
        when(partyVerifier.verify(PartyType.SUPPLIER, 9L)).thenThrow(new ResourceNotFoundException("Fournisseur", 9L));

        assertThrows(ResourceNotFoundException.class, () -> reader.findByParty(PartyType.SUPPLIER, 9L));
    }

    private Interaction interaction(Long id, String occurredAt, String summary) {
        Interaction interaction = new Interaction();
        interaction.setId(id);
        interaction.setPartyType(PartyType.CUSTOMER);
        interaction.setPartyId(1L);
        interaction.setType(InteractionType.NOTE);
        interaction.setOccurredAt(Instant.parse(occurredAt));
        interaction.setSummary(summary);
        interaction.setAuthor("crm@erp.local");
        return interaction;
    }
}
