package com.hackaton.vente.customer.service.reader;

import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.CustomerType;

public interface CustomerReader {

    CustomerResponse findById(Long id);

    PageResult<CustomerResponse> search(String search, CustomerType type, Integer page, Integer size);
}
