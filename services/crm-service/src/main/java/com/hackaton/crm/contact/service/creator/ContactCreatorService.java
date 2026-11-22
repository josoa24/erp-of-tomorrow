package com.hackaton.crm.contact.service.creator;

import com.hackaton.crm.contact.dto.ContactRequest;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.mapper.ContactMapper;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.NewCustomer;
import com.hackaton.crm.party.client.NewSupplier;
import com.hackaton.crm.party.client.SupplierClient;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactCreatorService implements ContactCreator {

    private final CustomerClient customerClient;
    private final SupplierClient supplierClient;
    private final ContactMapper contactMapper;

    @Override
    public ContactResponse create(ContactRequest request) {
        if (request.kind() == PartyType.CUSTOMER) {
            return contactMapper.fromCustomer(customerClient.create(toCustomer(request)), null, BigDecimal.ZERO);
        }
        return contactMapper.fromSupplier(supplierClient.create(toSupplier(request)), null);
    }

    private static NewCustomer toCustomer(ContactRequest request) {
        return new NewCustomer(
                request.name().trim(),
                request.phone(),
                request.email(),
                request.city(),
                request.type() == null ? null : request.type().name(),
                request.nif(),
                request.stat(),
                request.creditLimit());
    }

    private static NewSupplier toSupplier(ContactRequest request) {
        return new NewSupplier(
                request.name().trim(), request.phone(), request.email(), request.city(), request.nif(), request.stat());
    }
}
