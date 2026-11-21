package com.hackaton.vente.customer.service.profile;

import com.hackaton.vente.customer.dto.CustomerProfileResponse;

public interface CustomerProfileReader {

    CustomerProfileResponse findProfile(Long customerId);
}
