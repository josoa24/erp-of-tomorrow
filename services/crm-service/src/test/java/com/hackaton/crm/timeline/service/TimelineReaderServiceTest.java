package com.hackaton.crm.timeline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hackaton.crm.common.exception.ResourceNotFoundException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import com.hackaton.crm.interaction.entity.Interaction;
import com.hackaton.crm.interaction.entity.InteractionType;
import com.hackaton.crm.interaction.repository.InteractionRepository;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.PartyInfo;
import com.hackaton.crm.party.client.RemoteOrder;
import com.hackaton.crm.party.client.RemoteSale;
import com.hackaton.crm.party.client.SupplierClient;
import com.hackaton.crm.party.service.PartyVerifier;
import com.hackaton.crm.timeline.dto.TimelineEntry;
import com.hackaton.crm.timeline.dto.TimelineKind;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimelineReaderServiceTest {

    @Mock
    private InteractionRepository interactionRepository;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private SupplierClient supplierClient;
    @Mock
    private PartyVerifier partyVerifier;

    private TimelineReaderService service;

    @BeforeEach
    void setUp() {
        service = new TimelineReaderService(
                interactionRepository, customerClient, supplierClient, partyVerifier);
    }

    private Interaction interaction(Long id, PartyType type, String occurredAt) {
        Interaction interaction = new Interaction();
        interaction.setId(id);
        interaction.setPartyType(type);
        interaction.setPartyId(1L);
        interaction.setType(InteractionType.APPEL);
        interaction.setOccurredAt(Instant.parse(occurredAt));
        interaction.setSummary("Appel " + id);
        interaction.setAuthor("crm@erp.local");
        return interaction;
    }

    @Test
    void shouldMergeInteractionsAndValidatedSales_mostRecentFirst() {
        when(partyVerifier.verify(PartyType.CUSTOMER, 1L)).thenReturn(new PartyInfo(1L, "Epicerie"));
        when(interactionRepository.findByPartyTypeAndPartyIdOrderByOccurredAtDesc(PartyType.CUSTOMER, 1L))
                .thenReturn(List.of(
                        interaction(2L, PartyType.CUSTOMER, "2026-11-18T10:00:00Z"),
                        interaction(1L, PartyType.CUSTOMER, "2026-11-05T10:00:00Z")));
        when(customerClient.findSales(1L)).thenReturn(List.of(
                new RemoteSale(10L, "VALIDATED", new BigDecimal("9000"), Instant.parse("2026-11-10T10:00:00Z")),
                new RemoteSale(11L, "DRAFT", new BigDecimal("500"), null),
                new RemoteSale(12L, "VALIDATED", new BigDecimal("4000"), Instant.parse("2026-11-20T10:00:00Z"))));

        List<TimelineEntry> timeline = service.findTimeline(PartyType.CUSTOMER, 1L);

        assertEquals(4, timeline.size());
        assertEquals(List.of("Vente #12", "APPEL", "Vente #10", "APPEL"),
                timeline.stream().map(TimelineEntry::title).toList());
        assertEquals(TimelineKind.SALE, timeline.get(0).kind());
        assertEquals(new BigDecimal("4000"), timeline.get(0).amount());
    }

    @Test
    void shouldUseReceptionDateForReceivedOrdersAndSkipDrafts() {
        when(partyVerifier.verify(PartyType.SUPPLIER, 1L)).thenReturn(new PartyInfo(1L, "Socolait"));
        when(interactionRepository.findByPartyTypeAndPartyIdOrderByOccurredAtDesc(PartyType.SUPPLIER, 1L))
                .thenReturn(List.of());
        when(supplierClient.findOrders(1L)).thenReturn(List.of(
                new RemoteOrder(1L, "RECEIVED", new BigDecimal("40000"),
                        Instant.parse("2026-11-01T00:00:00Z"), Instant.parse("2026-11-05T00:00:00Z")),
                new RemoteOrder(2L, "VALIDATED", new BigDecimal("900"), Instant.parse("2026-11-08T00:00:00Z"), null),
                new RemoteOrder(3L, "DRAFT", new BigDecimal("100"), null, null)));

        List<TimelineEntry> timeline = service.findTimeline(PartyType.SUPPLIER, 1L);

        assertEquals(2, timeline.size());
        assertEquals("Bon de commande #2", timeline.get(0).title());
        assertEquals(Instant.parse("2026-11-05T00:00:00Z"), timeline.get(1).date());
        assertEquals(TimelineKind.PURCHASE_ORDER, timeline.get(1).kind());
    }

    @Test
    void shouldReturnEmptyTimeline_whenPartyHasNoActivity() {
        when(partyVerifier.verify(PartyType.CUSTOMER, 1L)).thenReturn(new PartyInfo(1L, "Epicerie"));
        when(interactionRepository.findByPartyTypeAndPartyIdOrderByOccurredAtDesc(PartyType.CUSTOMER, 1L))
                .thenReturn(List.of());
        when(customerClient.findSales(1L)).thenReturn(List.of());

        assertTrue(service.findTimeline(PartyType.CUSTOMER, 1L).isEmpty());
    }

    @Test
    void shouldFail_whenPartyDoesNotExist() {
        when(partyVerifier.verify(PartyType.CUSTOMER, 9L)).thenThrow(new ResourceNotFoundException("Client", 9L));

        assertThrows(ResourceNotFoundException.class, () -> service.findTimeline(PartyType.CUSTOMER, 9L));
    }

    @Test
    void shouldPropagateUnavailability_whenVenteServiceIsDown() {
        when(partyVerifier.verify(PartyType.CUSTOMER, 1L)).thenReturn(new PartyInfo(1L, "Epicerie"));
        when(interactionRepository.findByPartyTypeAndPartyIdOrderByOccurredAtDesc(PartyType.CUSTOMER, 1L))
                .thenReturn(List.of());
        when(customerClient.findSales(1L)).thenThrow(new ServiceUnavailableException("Le service Vente est indisponible"));

        assertThrows(ServiceUnavailableException.class, () -> service.findTimeline(PartyType.CUSTOMER, 1L));
    }
}
