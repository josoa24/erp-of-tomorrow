package com.hackaton.crm.contact.mapper;

import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.CustomerCategory;
import com.hackaton.crm.contact.service.policy.FollowUpPolicy;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.RemoteCustomer;
import com.hackaton.crm.party.client.RemoteSupplier;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactMapper {

    public static final String PROFESSIONAL_CUSTOMER = "Client · professionnel";
    public static final String INDIVIDUAL_CUSTOMER = "Client · particulier";
    public static final String SUPPLIER = "Fournisseur";

    private final FollowUpPolicy followUpPolicy;

    public ContactResponse fromCustomer(RemoteCustomer customer, Instant lastInteractionAt, BigDecimal outstanding) {
        boolean professional = CustomerCategory.ENTREPRISE.name().equals(customer.type());
        BigDecimal limit = customer.creditLimit();
        return new ContactResponse(
                PartyType.CUSTOMER,
                customer.id(),
                customer.name(),
                Initials.of(customer.name()),
                professional ? PROFESSIONAL_CUSTOMER : INDIVIDUAL_CUSTOMER,
                customer.city(),
                customer.phone(),
                lastInteractionAt,
                followUpPolicy.statusOf(lastInteractionAt, customer.createdAt()),
                customer.nif(),
                customer.stat(),
                limit,
                outstanding,
                limit != null && outstanding.compareTo(limit) > 0,
                professional,
                customer.email());
    }

    public ContactResponse fromSupplier(RemoteSupplier supplier, Instant lastInteractionAt) {
        return new ContactResponse(
                PartyType.SUPPLIER,
                supplier.id(),
                supplier.name(),
                Initials.of(supplier.name()),
                SUPPLIER,
                supplier.city(),
                supplier.phone(),
                lastInteractionAt,
                followUpPolicy.statusOf(lastInteractionAt, supplier.createdAt()),
                supplier.nif(),
                supplier.stat(),
                null,
                null,
                false,
                false,
                supplier.email());
    }
}
