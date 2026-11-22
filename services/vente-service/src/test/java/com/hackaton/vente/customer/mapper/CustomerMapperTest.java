package com.hackaton.vente.customer.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hackaton.vente.customer.dto.CustomerRequest;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.entity.CustomerType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapper();

    private CustomerRequest request(String nif, String stat, BigDecimal creditLimit) {
        return new CustomerRequest("Hotely Soa", "0341234567", null, null, "Toamasina",
                CustomerType.ENTREPRISE, null, nif, stat, creditLimit);
    }

    @Test
    void shouldCopyNifStatAndCreditLimit_whenTheyAreGiven() {
        Customer customer = mapper.toEntity(request(" 4000123456 ", "47111 11 2020 0 00123", new BigDecimal("2000000")));

        CustomerResponse response = mapper.toResponse(customer);

        assertEquals("4000123456", response.nif());
        assertEquals("47111 11 2020 0 00123", response.stat());
        assertEquals(new BigDecimal("2000000"), response.creditLimit());
    }

    @Test
    void shouldStoreNull_whenNifAndStatAreBlank() {
        Customer customer = mapper.toEntity(request("  ", "", null));

        assertNull(customer.getNif());
        assertNull(customer.getStat());
        assertNull(customer.getCreditLimit());
    }

    @Test
    void shouldClearTheCreditLimit_whenUpdateOmitsIt() {
        Customer customer = mapper.toEntity(request(null, null, new BigDecimal("1500000")));

        mapper.apply(customer, request(null, null, null));

        assertNull(customer.getCreditLimit());
    }
}
