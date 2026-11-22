package com.hackaton.crm.contact.service.reader;

import com.hackaton.crm.contact.dto.ContactResponse;
import java.util.Comparator;

public final class ContactOrder {

    public static final Comparator<ContactResponse> MOST_RECENT_EXCHANGE_FIRST = Comparator
            .comparing(ContactResponse::lastInteractionAt, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(ContactResponse::name, String.CASE_INSENSITIVE_ORDER);

    private ContactOrder() {
    }
}
