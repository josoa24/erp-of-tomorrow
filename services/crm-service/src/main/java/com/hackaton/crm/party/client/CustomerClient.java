package com.hackaton.crm.party.client;

import java.util.List;

public interface CustomerClient {

    PartyInfo findById(Long customerId);

    List<RemoteSale> findSales(Long customerId);
}
