package com.hackaton.crm.contact.service.source;

import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.mapper.ContactMapper;
import com.hackaton.crm.interaction.repository.InteractionRepository;
import com.hackaton.crm.interaction.repository.LastInteraction;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.RemoteCredit;
import com.hackaton.crm.party.client.SupplierClient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactSourceService implements ContactSource {

    private final InteractionRepository interactionRepository;
    private final CustomerClient customerClient;
    private final SupplierClient supplierClient;
    private final ContactMapper contactMapper;

    @Override
    public List<ContactResponse> load(ContactKind kind) {
        Map<PartyType, Map<Long, Instant>> lastInteractions = lastInteractionsByParty();
        List<ContactResponse> contacts = new ArrayList<>();
        if (kind != ContactKind.SUPPLIER) {
            Map<Long, Instant> customerInteractions = lastInteractions.getOrDefault(PartyType.CUSTOMER, Map.of());
            Map<Long, BigDecimal> outstanding = outstandingByCustomer();
            customerClient.findAll().forEach(customer -> contacts.add(contactMapper.fromCustomer(customer,
                    customerInteractions.get(customer.id()),
                    outstanding.getOrDefault(customer.id(), BigDecimal.ZERO))));
        }
        if (kind != ContactKind.CUSTOMER) {
            Map<Long, Instant> supplierInteractions = lastInteractions.getOrDefault(PartyType.SUPPLIER, Map.of());
            supplierClient.findAll().forEach(supplier -> contacts.add(
                    contactMapper.fromSupplier(supplier, supplierInteractions.get(supplier.id()))));
        }
        return contacts;
    }

    private Map<PartyType, Map<Long, Instant>> lastInteractionsByParty() {
        return interactionRepository.findLastInteractions().stream()
                .collect(Collectors.groupingBy(LastInteraction::partyType,
                        Collectors.toMap(LastInteraction::partyId, LastInteraction::occurredAt)));
    }

    private Map<Long, BigDecimal> outstandingByCustomer() {
        return customerClient.findCredits().stream()
                .filter(credit -> credit.customerId() != null && credit.outstanding() != null)
                .collect(Collectors.toMap(RemoteCredit::customerId, RemoteCredit::outstanding, BigDecimal::add));
    }
}
