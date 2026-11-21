package com.hackaton.vente.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.entity.CustomerType;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.customer.service.reader.CustomerReaderService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerReaderServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerReaderService service;

    private Customer customer(Long id, String name, String phone, String email, String city) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setCity(city);
        return customer;
    }

    @BeforeEach
    void setUp() {
        service = new CustomerReaderService(customerRepository, new CustomerMapper());
    }

    private void givenCustomers() {
        when(customerRepository.findAllByOrderByNameAsc()).thenReturn(List.of(
                customer(1L, "Epicerie Rakoto", "0340000001", "rakoto@mail.mg", "Antananarivo"),
                customer(2L, "Hotely Soa", "0320000002", "soa@mail.mg", "Toamasina"),
                customer(3L, "Mandresy", null, null, "Antsirabe")));
    }

    @Test
    void shouldReturnAllCustomers_whenNoSearchTerm() {
        givenCustomers();

        PageResult<CustomerResponse> result = service.search(null, null, null);

        assertEquals(3, result.total());
        assertEquals(3, result.items().size());
    }

    @Test
    void shouldMatchNamePhoneEmailAndCity_ignoringCase() {
        givenCustomers();

        assertEquals(1, service.search("RAKOTO", null, null).items().size());
        assertEquals(1, service.search("0320000002", null, null).items().size());
        assertEquals(1, service.search("soa@mail", null, null).items().size());
        assertEquals(1, service.search("antsirabe", null, null).items().size());
        assertTrue(service.search("introuvable", null, null).items().isEmpty());
    }

    @Test
    void shouldPaginateAndKeepTheTotal() {
        givenCustomers();

        PageResult<CustomerResponse> second = service.search(null, 1, 2);

        assertEquals(3, second.total());
        assertEquals(1, second.items().size());
        assertEquals("Mandresy", second.items().get(0).name());
    }

    @Test
    void shouldClampPageSizeAndReturnEmptyPastTheEnd() {
        givenCustomers();

        assertEquals(1, service.search(null, 0, 0).items().size());
        assertEquals(3, service.search(null, 0, 500).items().size());
        assertTrue(service.search(null, 9, 2).items().isEmpty());
        assertEquals(3, service.search(null, -4, 10).items().size());
    }

    @Test
    void shouldDefaultTypeToParticulier_whenNotSet() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer(1L, "A", null, null, null)));

        assertEquals(CustomerType.PARTICULIER, service.findById(1L).type());
    }

    @Test
    void shouldThrowNotFound_whenCustomerDoesNotExist() {
        when(customerRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }
}
