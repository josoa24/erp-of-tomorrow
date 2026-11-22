package com.hackaton.crm.contact.dto;

import com.hackaton.crm.party.PartyType;
import java.math.BigDecimal;
import java.time.Instant;

public record ContactResponse(
        PartyType kind,
        Long partyId,
        String name,
        String initials,
        String typeLabel,
        String city,
        String phone,
        Instant lastInteractionAt,
        ContactStatus status,
        String nif,
        String stat,
        BigDecimal creditLimit,
        BigDecimal outstanding,
        boolean overLimit,
        boolean professional,
        String email) {
}
