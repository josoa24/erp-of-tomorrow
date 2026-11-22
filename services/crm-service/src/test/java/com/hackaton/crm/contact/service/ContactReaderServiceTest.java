package com.hackaton.crm.contact.service;

import static com.hackaton.crm.contact.ContactFixtures.contact;
import static com.hackaton.crm.contact.dto.ContactStatus.ACTIVE;
import static com.hackaton.crm.contact.dto.ContactStatus.FOLLOW_UP;
import static com.hackaton.crm.party.PartyType.CUSTOMER;
import static com.hackaton.crm.party.PartyType.SUPPLIER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.crm.contact.dto.ContactCountsResponse;
import com.hackaton.crm.contact.dto.ContactFilter;
import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.service.reader.ContactReaderService;
import com.hackaton.crm.contact.service.source.ContactSource;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactReaderServiceTest {

    private static final List<ContactResponse> CONTACTS = List.of(
            contact(CUSTOMER, "Rasoa Andria", null, FOLLOW_UP),
            contact(SUPPLIER, "Socolait", "2026-11-10T07:00:00Z", ACTIVE),
            contact(CUSTOMER, "Hotely Soa", "2026-11-20T07:00:00Z", ACTIVE, true, BigDecimal.ZERO, false),
            contact(CUSTOMER, "Hery Rabe", null, FOLLOW_UP, true, BigDecimal.ZERO, false));

    @Mock
    private ContactSource contactSource;

    private ContactReaderService service;

    @BeforeEach
    void setUp() {
        service = new ContactReaderService(contactSource);
    }

    private List<String> names(ContactFilter filter) {
        return service.search(filter).stream().map(ContactResponse::name).toList();
    }

    @Test
    void shouldSortByMostRecentExchange_withContactsWithoutExchangeLastByName() {
        when(contactSource.load(ContactKind.ALL)).thenReturn(CONTACTS);

        assertEquals(List.of("Hotely Soa", "Socolait", "Hery Rabe", "Rasoa Andria"),
                names(new ContactFilter(ContactKind.ALL, null, false, false)));
    }

    @Test
    void shouldKeepOnlyContactsToFollowUp_whenFollowUpIsRequested() {
        when(contactSource.load(ContactKind.ALL)).thenReturn(CONTACTS);

        assertEquals(List.of("Hery Rabe", "Rasoa Andria"), names(new ContactFilter(ContactKind.ALL, null, true, false)));
    }

    @Test
    void shouldLoadOnlyCustomersAndKeepCompanies_whenProfessionalIsRequested() {
        when(contactSource.load(ContactKind.CUSTOMER)).thenReturn(CONTACTS);

        assertEquals(List.of("Hotely Soa", "Hery Rabe"), names(new ContactFilter(ContactKind.ALL, null, false, true)));
    }

    @Test
    void shouldMatchNameIgnoringCase_whenSearching() {
        when(contactSource.load(ContactKind.SUPPLIER)).thenReturn(CONTACTS);

        assertEquals(List.of("Socolait"), names(new ContactFilter(ContactKind.SUPPLIER, "SOCO", false, false)));
    }

    @Test
    void shouldCountEachTab_whenNoSearchIsGiven() {
        when(contactSource.load(ContactKind.ALL)).thenReturn(CONTACTS);

        assertEquals(new ContactCountsResponse(4, 3, 1, 2, 2), service.count(null));
    }

    @Test
    void shouldCountOnlyMatchingContacts_whenSearchIsGiven() {
        when(contactSource.load(ContactKind.ALL)).thenReturn(CONTACTS);

        assertEquals(new ContactCountsResponse(1, 1, 0, 1, 1), service.count("hery"));
    }
}
