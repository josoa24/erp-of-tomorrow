package com.hackaton.stock.supplier.mapper;

import com.hackaton.stock.common.util.BankAccounts;
import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.entity.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierRequest request) {
        Supplier supplier = new Supplier();
        apply(supplier, request);
        return supplier;
    }

    public void apply(Supplier supplier, SupplierRequest request) {
        supplier.setName(request.name().trim());
        supplier.setPhone(request.phone());
        supplier.setEmail(request.email());
        supplier.setAddress(request.address());
        supplier.setPaymentTerms(request.paymentTerms());
        supplier.setUsualDeliveryDays(request.usualDeliveryDays());
        supplier.setBankAccount(BankAccounts.clean(request.bankAccount()));
        supplier.setCity(blankToNull(request.city()));
        supplier.setNif(blankToNull(request.nif()));
        supplier.setStat(blankToNull(request.stat()));
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress(),
                supplier.getPaymentTerms(),
                supplier.getUsualDeliveryDays(),
                supplier.getCreatedAt(),
                BankAccounts.last4(supplier.getBankAccount()),
                supplier.getCity(),
                supplier.getNif(),
                supplier.getStat());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
