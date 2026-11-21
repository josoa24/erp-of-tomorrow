package com.hackaton.vente.customer.service.updater;

import com.hackaton.vente.customer.dto.CustomerRequest;
import com.hackaton.vente.customer.dto.CustomerResponse;

public interface CustomerUpdater {

    CustomerResponse update(Long id, CustomerRequest request);
}
