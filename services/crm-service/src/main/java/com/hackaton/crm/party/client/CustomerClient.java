package com.hackaton.crm.party.client;

import java.time.LocalDate;
import java.util.List;

public interface CustomerClient {

    PartyInfo findById(Long customerId);

    List<RemoteSale> findSales(Long customerId);

    List<RemoteCustomer> findAll();

    RemoteCustomer create(NewCustomer customer);

    List<RemoteCredit> findCredits();

    List<RemoteCustomerSale> findValidatedSalesSince(LocalDate from);
}
