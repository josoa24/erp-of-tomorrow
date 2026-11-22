package com.hackaton.crm.contact.dto;

import com.hackaton.crm.party.PartyType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ContactRequest(
        @NotNull PartyType kind,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 40) String phone,
        @Email @Size(max = 255) String email,
        @Size(max = 80) String city,
        CustomerCategory type,
        @Size(max = 20) String nif,
        @Size(max = 30) String stat,
        @PositiveOrZero @Digits(integer = 12, fraction = 2) BigDecimal creditLimit) {

    @AssertTrue(message = "le type et le plafond de crédit sont réservés aux clients")
    public boolean isSupplierWithoutCustomerFields() {
        return kind != PartyType.SUPPLIER || (type == null && creditLimit == null);
    }
}
