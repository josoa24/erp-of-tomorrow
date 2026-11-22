package com.hackaton.crm.contact;

import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.ContactStatus;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.RemoteCustomer;
import com.hackaton.crm.party.client.RemoteSupplier;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public final class ContactFixtures {

    public static final ZoneId ANTANANARIVO = ZoneId.of("Indian/Antananarivo");
    public static final Instant NOW = Instant.parse("2026-11-22T06:00:00Z");
    public static final Clock CLOCK = Clock.fixed(NOW, ANTANANARIVO);

    private ContactFixtures() {
    }

    public static RemoteCustomer customer(Long id, String name, String type, BigDecimal creditLimit) {
        return new RemoteCustomer(id, name, "0341234567", null, "Antananarivo", type, "4000123456",
                "47111 11 2020 0 00123", creditLimit, Instant.parse("2026-11-01T08:00:00Z"));
    }

    public static RemoteSupplier supplier(Long id, String name) {
        return new RemoteSupplier(id, name, "0331234567", "contact@socolait.mg", "Antsirabe", null, null,
                Instant.parse("2026-01-10T08:00:00Z"));
    }

    public static ContactResponse contact(PartyType kind, String name, String lastInteractionAt, ContactStatus status) {
        return contact(kind, name, lastInteractionAt, status, false, BigDecimal.ZERO, false);
    }

    public static ContactResponse contact(PartyType kind, String name, String lastInteractionAt, ContactStatus status,
            boolean professional, BigDecimal outstanding, boolean overLimit) {
        Instant last = lastInteractionAt == null ? null : Instant.parse(lastInteractionAt);
        return new ContactResponse(kind, (long) name.length(), name, "XX", "libellé", "Toamasina", "0320000000",
                last, status, null, null, null, kind == PartyType.CUSTOMER ? outstanding : null, overLimit,
                professional, null);
    }
}
