package com.hackaton.module.reference.service;

import com.hackaton.module.reference.client.RemoteEmployee;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ReferenceLabels {

    private static final String SALE_PREFIX = "V-";
    private static final String SEPARATOR = " · ";

    private ReferenceLabels() {
    }

    public static String sale(Long saleId, String customerName) {
        String number = SALE_PREFIX + String.format("%04d", saleId);
        return customerName == null || customerName.isBlank() ? number : number + SEPARATOR + customerName.trim();
    }

    public static String employee(RemoteEmployee employee) {
        return Stream.of(employee.firstName(), employee.lastName())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
