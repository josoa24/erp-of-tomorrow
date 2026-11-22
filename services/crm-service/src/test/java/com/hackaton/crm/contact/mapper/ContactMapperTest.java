package com.hackaton.crm.contact.mapper;

import static com.hackaton.crm.contact.ContactFixtures.CLOCK;
import static com.hackaton.crm.contact.ContactFixtures.customer;
import static com.hackaton.crm.contact.ContactFixtures.supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.ContactStatus;
import com.hackaton.crm.contact.service.policy.FollowUpPolicy;
import com.hackaton.crm.party.PartyType;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ContactMapperTest {

    private static final BigDecimal LIMIT = new BigDecimal("1500000");

    private final ContactMapper mapper = new ContactMapper(new FollowUpPolicy(CLOCK, 30));

    private ContactResponse company(BigDecimal limit, String outstanding) {
        return mapper.fromCustomer(customer(1L, "Hotely Soa", "ENTREPRISE", limit), null, new BigDecimal(outstanding));
    }

    @Test
    void shouldLabelAsProfessional_whenCustomerIsACompany() {
        ContactResponse contact = company(LIMIT, "0");

        assertEquals("Client · professionnel", contact.typeLabel());
        assertTrue(contact.professional());
        assertEquals("HS", contact.initials());
        assertEquals("4000123456", contact.nif());
    }

    @Test
    void shouldLabelAsIndividual_whenCustomerTypeIsParticulierOrMissing() {
        assertEquals("Client · particulier",
                mapper.fromCustomer(customer(2L, "Hery Rabe", "PARTICULIER", null), null, BigDecimal.ZERO).typeLabel());
        assertFalse(mapper.fromCustomer(customer(3L, "Rasoa", null, null), null, BigDecimal.ZERO).professional());
    }

    @Test
    void shouldFlagOverLimit_whenOutstandingExceedsTheCreditLimit() {
        assertTrue(company(LIMIT, "1800000").overLimit());
    }

    @Test
    void shouldNotFlagOverLimit_whenOutstandingEqualsTheCreditLimit() {
        assertFalse(company(LIMIT, "1500000").overLimit());
    }

    @Test
    void shouldNotFlagOverLimit_whenCustomerHasNoCreditLimit() {
        assertFalse(company(null, "9000000").overLimit());
    }

    @Test
    void shouldComputeStatusFromLastExchange_whenCustomerHasOne() {
        ContactResponse contact = mapper.fromCustomer(customer(2L, "Hery Rabe", null, null),
                Instant.parse("2026-11-15T08:00:00Z"), BigDecimal.ZERO);

        assertEquals(ContactStatus.ACTIVE, contact.status());
        assertEquals(Instant.parse("2026-11-15T08:00:00Z"), contact.lastInteractionAt());
    }

    @Test
    void shouldMapSupplierWithoutCreditFields() {
        ContactResponse contact = mapper.fromSupplier(supplier(4L, "Socolait"), null);

        assertEquals(PartyType.SUPPLIER, contact.kind());
        assertEquals("Fournisseur", contact.typeLabel());
        assertEquals("Antsirabe", contact.city());
        assertNull(contact.outstanding());
        assertFalse(contact.overLimit());
        assertEquals(ContactStatus.FOLLOW_UP, contact.status());
    }
}
