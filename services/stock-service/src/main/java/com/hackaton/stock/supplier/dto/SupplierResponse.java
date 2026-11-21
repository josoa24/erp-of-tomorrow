package com.hackaton.stock.supplier.dto;

import java.time.Instant;

public record SupplierResponse(Long id, String name, String phone, String email, String address, Instant createdAt) {
}
