package com.hackaton.crm.contact.service.reader;

import com.hackaton.crm.contact.dto.ContactResponse;
import java.util.Locale;
import java.util.stream.Stream;

public final class ContactMatcher {

    private ContactMatcher() {
    }

    public static boolean matches(ContactResponse contact, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String term = search.trim().toLowerCase(Locale.ROOT);
        return Stream.of(contact.name(), contact.phone(), contact.email(), contact.city(), contact.nif(), contact.stat())
                .anyMatch(value -> value != null && value.toLowerCase(Locale.ROOT).contains(term));
    }
}
