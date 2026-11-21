package com.hackaton.crm.party.client;

import java.util.List;

public interface SupplierClient {

    PartyInfo findById(Long supplierId);

    List<RemoteOrder> findOrders(Long supplierId);
}
