package com.hackaton.crm.contact.service.reader;

import com.hackaton.crm.contact.dto.ContactCountsResponse;
import com.hackaton.crm.contact.dto.ContactFilter;
import com.hackaton.crm.contact.dto.ContactResponse;
import java.util.List;

public interface ContactReader {

    List<ContactResponse> search(ContactFilter filter);

    ContactCountsResponse count(String search);
}
