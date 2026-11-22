package com.hackaton.crm.contact.service;

import static com.hackaton.crm.contact.ContactFixtures.CLOCK;
import static com.hackaton.crm.contact.ContactFixtures.customer;
import static com.hackaton.crm.contact.ContactFixtures.supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.contact.dto.ContactRequest;
import com.hackaton.crm.contact.dto.ContactResponse;
import com.hackaton.crm.contact.dto.CustomerCategory;
import com.hackaton.crm.contact.mapper.ContactMapper;
import com.hackaton.crm.contact.service.creator.ContactCreatorService;
import com.hackaton.crm.contact.service.policy.FollowUpPolicy;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.party.client.CustomerClient;
import com.hackaton.crm.party.client.NewCustomer;
import com.hackaton.crm.party.client.NewSupplier;
import com.hackaton.crm.party.client.SupplierClient;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactCreatorServiceTest {

    private static final BigDecimal LIMIT = new BigDecimal("2000000");

    @Mock
    private CustomerClient customerClient;
    @Mock
    private SupplierClient supplierClient;

    private ContactCreatorService service;

    @BeforeEach
    void setUp() {
        service = new ContactCreatorService(customerClient, supplierClient,
                new ContactMapper(new FollowUpPolicy(CLOCK, 30)));
    }

    @Test
    void shouldForwardCompanyFieldsToVente_whenContactIsACustomer() {
        when(customerClient.create(any())).thenReturn(customer(9L, "Hotely Soa", "ENTREPRISE", LIMIT));
        ContactRequest request = new ContactRequest(PartyType.CUSTOMER, "  Hotely Soa ", "0341234567", null,
                "Toamasina", CustomerCategory.ENTREPRISE, "4000123456", "47111 11 2020 0 00123", LIMIT);

        ContactResponse contact = service.create(request);

        verify(customerClient).create(new NewCustomer("Hotely Soa", "0341234567", null, "Toamasina", "ENTREPRISE",
                "4000123456", "47111 11 2020 0 00123", LIMIT));
        assertEquals(9L, contact.partyId());
        assertEquals("Client · professionnel", contact.typeLabel());
        assertEquals(BigDecimal.ZERO, contact.outstanding());
        verifyNoInteractions(supplierClient);
    }

    @Test
    void shouldForwardToStock_whenContactIsASupplier() {
        when(supplierClient.create(any())).thenReturn(supplier(5L, "Socolait"));
        ContactRequest request = new ContactRequest(PartyType.SUPPLIER, "Socolait", null, "contact@socolait.mg",
                "Antsirabe", null, null, null, null);

        ContactResponse contact = service.create(request);

        verify(supplierClient).create(new NewSupplier("Socolait", null, "contact@socolait.mg", "Antsirabe", null, null));
        assertEquals("Fournisseur", contact.typeLabel());
        verifyNoInteractions(customerClient);
    }

    @Test
    void shouldPropagateTheRefusal_whenStockRejectsTheSupplier() {
        when(supplierClient.create(any())).thenThrow(new BusinessException("Le fournisseur Socolait existe déjà"));
        ContactRequest request = new ContactRequest(PartyType.SUPPLIER, "Socolait", null, null, null, null, null,
                null, null);

        assertThrows(BusinessException.class, () -> service.create(request));
    }
}
