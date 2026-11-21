package com.hackaton.vente.sale.service.validator;

import com.hackaton.vente.sale.dto.SaleResponse;

public interface SaleValidator {

    SaleResponse validate(Long id);
}
