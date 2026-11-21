package com.hackaton.crm.party.service;

import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.PartyInfo;

public interface PartyVerifier {

    PartyInfo verify(PartyType type, Long id);
}
