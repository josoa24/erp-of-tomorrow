package com.hackaton.vente.customer.service.reader;

import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.customer.dto.CustomerResponse;

public interface CustomerReader {

    CustomerResponse findById(Long id);

    PageResult<CustomerResponse> search(String search, Integer page, Integer size);
}
