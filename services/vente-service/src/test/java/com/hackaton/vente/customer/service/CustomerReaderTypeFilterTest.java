package com.hackaton.vente.customer.service;

import static com.hackaton.vente.customer.service.CustomerEntities.customer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.CustomerType;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.customer.service.reader.CustomerReaderService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerReaderTypeFilterTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerReaderService service;

    @BeforeEach
    void setUp() {
        service = new CustomerReaderService(customerRepository, new CustomerMapper());
        when(customerRepository.findAllByOrderByNameAsc()).thenReturn(List.of(
                customer(1L, "Hotely Soa", CustomerType.ENTREPRISE),
                customer(2L, "Hery Rabe", CustomerType.PARTICULIER),
                customer(3L, "Rasoa Andria", null)));
    }

    private List<String> names(CustomerType type, String search) {
        return service.search(search, type, null, null).items().stream().map(CustomerResponse::name).toList();
    }

    @Test
    void shouldKeepOnlyCompanies_whenTypeIsEntreprise() {
        assertEquals(List.of("Hotely Soa"), names(CustomerType.ENTREPRISE, null));
    }

    @Test
    void shouldTreatMissingTypeAsParticulier_whenTypeIsParticulier() {
        assertEquals(List.of("Hery Rabe", "Rasoa Andria"), names(CustomerType.PARTICULIER, null));
    }

    @Test
    void shouldCombineTypeAndSearch_whenBothAreGiven() {
        assertEquals(List.of("Rasoa Andria"), names(CustomerType.PARTICULIER, "andria"));
    }

    @Test
    void shouldReturnEveryType_whenTypeIsAbsent() {
        assertEquals(3, names(null, null).size());
    }
}
