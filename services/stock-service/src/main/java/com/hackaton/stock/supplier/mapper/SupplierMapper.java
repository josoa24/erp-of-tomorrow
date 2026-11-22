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
                BankAccounts.last4(supplier.getBankAccount()));
    }
}
