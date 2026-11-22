package com.hackaton.crm.contact.service;

import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.ContactStatus;
import com.hackaton.crm.party.PartyType;
import java.util.List;
import java.util.function.Predicate;

public final class ContactTally {

    public static final Predicate<ContactResponse> CUSTOMER = contact -> contact.kind() == PartyType.CUSTOMER;
    public static final Predicate<ContactResponse> SUPPLIER = contact -> contact.kind() == PartyType.SUPPLIER;
    public static final Predicate<ContactResponse> FOLLOW_UP = contact -> contact.status() == ContactStatus.FOLLOW_UP;

    private ContactTally() {
    }

    public static long countMatching(List<ContactResponse> contacts, Predicate<ContactResponse> predicate) {
        return contacts.stream().filter(predicate).count();
    }
}
