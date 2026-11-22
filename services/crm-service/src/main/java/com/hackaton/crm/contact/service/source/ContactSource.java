package com.hackaton.crm.contact.service.source;

import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactResponse;
import java.util.List;

public interface ContactSource {

    List<ContactResponse> load(ContactKind kind);
}
