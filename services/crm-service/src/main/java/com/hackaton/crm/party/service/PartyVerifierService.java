package com.hackaton.crm.party.service;

import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.PartyInfo;
import com.hackaton.crm.party.client.SupplierClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyVerifierService implements PartyVerifier {

    private final CustomerClient customerClient;
    private final SupplierClient supplierClient;

    @Override
    public PartyInfo verify(PartyType type, Long id) {
        return type == PartyType.CUSTOMER ? customerClient.findById(id) : supplierClient.findById(id);
    }
}
