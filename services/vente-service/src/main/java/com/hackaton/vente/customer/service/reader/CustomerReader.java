package com.hackaton.vente.customer.service.reader;

import com.hackaton.vente.customer.dto.CustomerResponse;
import java.util.List;

public interface CustomerReader {

    CustomerResponse findById(Long id);

    List<CustomerResponse> findAll();
}
