package com.hackaton.crm.contact.service.creator;

import com.hackaton.crm.contact.dto.ContactRequest;
import com.hackaton.crm.contact.dto.ContactResponse;

public interface ContactCreator {

    ContactResponse create(ContactRequest request);
}
