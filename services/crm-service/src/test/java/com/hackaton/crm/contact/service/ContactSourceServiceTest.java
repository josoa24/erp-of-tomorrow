package com.hackaton.crm.contact.service;

import static com.hackaton.crm.contact.ContactFixtures.CLOCK;
import static com.hackaton.crm.contact.ContactFixtures.customer;
import static com.hackaton.crm.contact.ContactFixtures.supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.crm.common.exception.ServiceUnavailableException;
import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.mapper.ContactMapper;
import com.hackaton.crm.contact.service.policy.FollowUpPolicy;
import com.hackaton.crm.contact.service.source.ContactSourceService;
import com.hackaton.crm.interaction.repository.InteractionRepository;
import com.hackaton.crm.interaction.repository.LastInteraction;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.RemoteCredit;
import com.hackaton.crm.party.client.SupplierClient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactSourceServiceTest {

    private static final Instant LAST_CALL = Instant.parse("2026-11-20T07:00:00Z");

    @Mock
    private InteractionRepository interactionRepository;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private SupplierClient supplierClient;

    private ContactSourceService service;

    @BeforeEach
    void setUp() {
        service = new ContactSourceService(interactionRepository, customerClient, supplierClient,
                new ContactMapper(new FollowUpPolicy(CLOCK, 30)));
        when(interactionRepository.findLastInteractions()).thenReturn(List.of(
                new LastInteraction(PartyType.CUSTOMER, 1L, LAST_CALL),
                new LastInteraction(PartyType.SUPPLIER, 1L, Instant.parse("2026-10-01T07:00:00Z"))));
    }

    @Test
    void shouldJoinLastExchangeAndOutstanding_whenLoadingEveryContact() {
        when(customerClient.findCredits()).thenReturn(List.of(new RemoteCredit(1L, new BigDecimal("1800000"), 1)));
        when(customerClient.findAll()).thenReturn(List.of(customer(1L, "Hotely Soa", "ENTREPRISE", null)));
        when(supplierClient.findAll()).thenReturn(List.of(supplier(1L, "Socolait")));

        List<ContactResponse> contacts = service.load(ContactKind.ALL);

        assertEquals(2, contacts.size());
        assertEquals(LAST_CALL, contacts.get(0).lastInteractionAt());
        assertEquals(new BigDecimal("1800000"), contacts.get(0).outstanding());
        assertEquals(Instant.parse("2026-10-01T07:00:00Z"), contacts.get(1).lastInteractionAt());
    }

    @Test
    void shouldLeaveLastExchangeEmptyAndOutstandingZero_whenCustomerHasNeither() {
        when(customerClient.findCredits()).thenReturn(List.of());
        when(customerClient.findAll()).thenReturn(List.of(customer(2L, "Hery Rabe", null, null)));

        ContactResponse contact = service.load(ContactKind.CUSTOMER).get(0);

        assertNull(contact.lastInteractionAt());
        assertEquals(BigDecimal.ZERO, contact.outstanding());
        verifyNoInteractions(supplierClient);
    }

    @Test
    void shouldNotCallVente_whenOnlySuppliersAreRequested() {
        when(supplierClient.findAll()).thenReturn(List.of(supplier(1L, "Socolait")));

        assertEquals(1, service.load(ContactKind.SUPPLIER).size());
        verifyNoInteractions(customerClient);
    }

    @Test
    void shouldPropagateUnavailability_whenVenteIsDown() {
        when(customerClient.findCredits()).thenThrow(new ServiceUnavailableException("Le service Vente est indisponible"));

        assertThrows(ServiceUnavailableException.class, () -> service.load(ContactKind.ALL));
    }
}
