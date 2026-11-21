package com.hackaton.vente.customer.service.creator;

import com.hackaton.vente.customer.dto.CustomerRequest;
import com.hackaton.vente.customer.dto.CustomerResponse;

public interface CustomerCreator {

    CustomerResponse create(CustomerRequest request);
}
