package com.hackaton.crm.party.client;

import java.math.BigDecimal;

public record NewCustomer(
        String name,
        String phone,
        String email,
        String city,
        String type,
        String nif,
        String stat,
        BigDecimal creditLimit) {
}
