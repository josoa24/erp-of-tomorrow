package com.hackaton.vente.payment.repository;

import java.math.BigDecimal;

public interface SalePaidTotal {

    Long getSaleId();

    BigDecimal getTotal();
}
