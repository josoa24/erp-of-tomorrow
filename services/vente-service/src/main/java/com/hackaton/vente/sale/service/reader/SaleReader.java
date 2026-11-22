package com.hackaton.vente.sale.service.reader;

import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;

public interface SaleReader {

    SaleResponse findById(Long id);

    PageResult<SaleResponse> search(SaleSearchCriteria criteria, Integer page, Integer size);
}
