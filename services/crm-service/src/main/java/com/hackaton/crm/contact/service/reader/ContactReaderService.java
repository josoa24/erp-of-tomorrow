package com.hackaton.crm.contact.service.reader;

import static com.hackaton.crm.contact.service.ContactTally.CUSTOMER;
import static com.hackaton.crm.contact.service.ContactTally.FOLLOW_UP;
import static com.hackaton.crm.contact.service.ContactTally.SUPPLIER;
import static com.hackaton.crm.contact.service.ContactTally.countMatching;

import com.hackaton.crm.contact.dto.ContactCountsResponse;
import com.hackaton.crm.contact.dto.ContactFilter;
import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.service.source.ContactSource;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactReaderService implements ContactReader {

    private final ContactSource contactSource;

    @Override
    public List<ContactResponse> search(ContactFilter filter) {
        ContactKind kind = filter.professional() ? ContactKind.CUSTOMER : filter.kind();
        return contactSource.load(kind).stream()
                .filter(contact -> ContactMatcher.matches(contact, filter.search()))
                .filter(contact -> !filter.followUp() || FOLLOW_UP.test(contact))
                .filter(contact -> !filter.professional() || contact.professional())
                .sorted(ContactOrder.MOST_RECENT_EXCHANGE_FIRST)
                .toList();
    }

    @Override
    public ContactCountsResponse count(String search) {
        List<ContactResponse> contacts = contactSource.load(ContactKind.ALL).stream()
                .filter(contact -> ContactMatcher.matches(contact, search))
                .toList();
        return new ContactCountsResponse(
                contacts.size(),
                countMatching(contacts, CUSTOMER),
                countMatching(contacts, SUPPLIER),
                countMatching(contacts, FOLLOW_UP),
                countMatching(contacts, ContactResponse::professional));
    }
}
