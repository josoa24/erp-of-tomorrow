package com.hackaton.crm.contact.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.ContactStatus;
import com.hackaton.crm.contact.service.reader.ContactMatcher;
import com.hackaton.crm.party.PartyType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ContactMatcherTest {

    private static final ContactResponse HOTELY = new ContactResponse(PartyType.CUSTOMER, 1L, "Hotely Soa", "HS",
            "Client · professionnel", "Toamasina", "0341234567", null, ContactStatus.ACTIVE, "4000123456",
            "47111 11 2020 0 00123", null, BigDecimal.ZERO, false, true, "contact@hotelysoa.mg");

    @Test
    void shouldMatchEveryContact_whenSearchIsBlank() {
        assertTrue(ContactMatcher.matches(HOTELY, "  "));
        assertTrue(ContactMatcher.matches(HOTELY, null));
    }

    @Test
    void shouldMatchCityPhoneEmailNifAndStat_whenTermIsContained() {
        assertTrue(ContactMatcher.matches(HOTELY, "toamasina"));
        assertTrue(ContactMatcher.matches(HOTELY, "034123"));
        assertTrue(ContactMatcher.matches(HOTELY, "@hotelysoa"));
        assertTrue(ContactMatcher.matches(HOTELY, "4000123"));
        assertTrue(ContactMatcher.matches(HOTELY, "2020 0"));
    }

    @Test
    void shouldNotMatch_whenNoFieldContainsTheTerm() {
        assertFalse(ContactMatcher.matches(HOTELY, "antsirabe"));
    }
}
