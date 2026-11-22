package com.hackaton.crm.contact.service;

import static com.hackaton.crm.contact.ContactFixtures.CLOCK;
import static com.hackaton.crm.contact.ContactFixtures.contact;
import static com.hackaton.crm.contact.dto.ContactStatus.ACTIVE;
import static com.hackaton.crm.contact.dto.ContactStatus.FOLLOW_UP;
import static com.hackaton.crm.party.PartyType.CUSTOMER;
import static com.hackaton.crm.party.PartyType.SUPPLIER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.crm.common.exception.ServiceUnavailableException;
import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactSummaryResponse;
import com.hackaton.crm.contact.service.source.ContactSource;
import com.hackaton.crm.contact.service.summary.ContactSummaryReaderService;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.RemoteCustomerSale;
import com.hackaton.crm.party.client.RemoteOrder;
import com.hackaton.crm.party.client.SupplierClient;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactSummaryReaderServiceTest {

    private static final LocalDate DAY_BEFORE_QUARTER = LocalDate.of(2026, 9, 30);

    @Mock
    private ContactSource contactSource;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private SupplierClient supplierClient;

    private ContactSummaryReaderService service;

    @BeforeEach
    void setUp() {
        service = new ContactSummaryReaderService(contactSource, customerClient, supplierClient, CLOCK);
    }

    private static RemoteCustomerSale sale(Long customerId, String validatedAt) {
        return new RemoteCustomerSale(customerId, Instant.parse(validatedAt));
    }

    @Test
    void shouldAggregateContactsCreditAndOpenOrders_whenEveryServiceAnswers() {
        when(contactSource.load(ContactKind.ALL)).thenReturn(List.of(
                contact(CUSTOMER, "Hotely Soa", null, ACTIVE, true, new BigDecimal("1800000"), true),
                contact(CUSTOMER, "Hery Rabe", null, FOLLOW_UP, false, new BigDecimal("200000"), false),
                contact(CUSTOMER, "Rasoa Andria", null, ACTIVE),
                contact(SUPPLIER, "Socolait", null, FOLLOW_UP)));
        when(customerClient.findValidatedSalesSince(DAY_BEFORE_QUARTER)).thenReturn(List.of());
        when(supplierClient.findOrdersByStatus("VALIDATED")).thenReturn(List.of(
                new RemoteOrder(1L, "VALIDATED", BigDecimal.TEN, null, null),
                new RemoteOrder(2L, "VALIDATED", BigDecimal.TEN, null, null)));

        ContactSummaryResponse summary = service.summarize();

        assertEquals(new ContactSummaryResponse(0, 3, 1, 2, 2, new BigDecimal("2000000"), 1), summary);
    }

    @Test
    void shouldCountDistinctCustomersWithSaleValidatedSinceQuarterStartInAntananarivo() {
        when(contactSource.load(ContactKind.ALL)).thenReturn(List.of());
        when(supplierClient.findOrdersByStatus("VALIDATED")).thenReturn(List.of());
        when(customerClient.findValidatedSalesSince(DAY_BEFORE_QUARTER)).thenReturn(List.of(
                sale(1L, "2026-10-05T08:00:00Z"),
                sale(1L, "2026-11-01T08:00:00Z"),
                sale(2L, "2026-09-30T21:30:00Z"),
                sale(3L, "2026-09-30T20:00:00Z"),
                sale(null, "2026-10-10T08:00:00Z"),
                new RemoteCustomerSale(4L, null)));

        assertEquals(2, service.summarize().activeCustomers());
    }

    @Test
    void shouldPropagateUnavailability_whenStockIsDown() {
        when(contactSource.load(ContactKind.ALL)).thenReturn(List.of());
        when(customerClient.findValidatedSalesSince(DAY_BEFORE_QUARTER)).thenReturn(List.of());
        when(supplierClient.findOrdersByStatus("VALIDATED"))
                .thenThrow(new ServiceUnavailableException("Le service Stock est indisponible"));

        assertThrows(ServiceUnavailableException.class, () -> service.summarize());
    }
}
