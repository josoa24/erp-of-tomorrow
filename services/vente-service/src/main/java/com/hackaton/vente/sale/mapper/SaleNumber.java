package com.hackaton.vente.sale.mapper;

public final class SaleNumber {

    private SaleNumber() {
    }

    public static String of(Long saleId) {
        return String.format("V-%04d", saleId);
    }
}
