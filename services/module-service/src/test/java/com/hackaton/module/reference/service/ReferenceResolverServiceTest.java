package com.hackaton.module.reference.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.definition.entity.ReferenceTarget;
import com.hackaton.module.reference.client.RemoteEmployee;
import com.hackaton.module.reference.client.RemoteNamed;
import com.hackaton.module.reference.client.RemoteSale;
import com.hackaton.module.reference.client.RhClient;
import com.hackaton.module.reference.client.StockClient;
import com.hackaton.module.reference.client.VenteClient;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReferenceResolverServiceTest {

    @Mock
    private VenteClient venteClient;
    @Mock
    private RhClient rhClient;
    @Mock
    private StockClient stockClient;

    private ReferenceResolverService resolver;

    @BeforeEach
    void setUp() {
        resolver = new ReferenceResolverService(venteClient, rhClient, stockClient);
    }

    @Test
    void shouldLabelCustomerWithName() {
        when(venteClient.findCustomer(7L)).thenReturn(Optional.of(new RemoteNamed(7L, "Hery Rabe")));

        assertEquals(new ReferenceValue(7L, "Hery Rabe"), resolver.resolve(ReferenceTarget.CUSTOMER, 7L));
    }

    @Test
    void shouldLabelSaleWithNumberAndCustomer() {
        when(venteClient.findSale(12L)).thenReturn(Optional.of(
                new RemoteSale(12L, 7L, "VALIDATED", new BigDecimal("45000"), null, null, true)));
        when(venteClient.findCustomer(7L)).thenReturn(Optional.of(new RemoteNamed(7L, "Hery Rabe")));

        assertEquals("V-0012 · Hery Rabe", resolver.resolve(ReferenceTarget.SALE, 12L).label());
    }

    @Test
    void shouldLabelSaleWithNumberOnly_whenItHasNoCustomer() {
        when(venteClient.findSale(13L)).thenReturn(Optional.of(new RemoteSale(13L, null, "DRAFT", null, null, null, null)));

        assertEquals("V-0013", resolver.resolve(ReferenceTarget.SALE, 13L).label());
    }

    @Test
    void shouldLabelEmployeeProductAndSupplier() {
        when(rhClient.findEmployee(3L)).thenReturn(Optional.of(new RemoteEmployee(3L, "Toky", "Rakoto")));
        when(stockClient.findProduct(4L)).thenReturn(Optional.of(new RemoteNamed(4L, "Riz blanc")));
        when(stockClient.findSupplier(5L)).thenReturn(Optional.of(new RemoteNamed(5L, "Socolait")));

        assertEquals("Toky Rakoto", resolver.resolve(ReferenceTarget.EMPLOYEE, 3L).label());
        assertEquals("Riz blanc", resolver.resolve(ReferenceTarget.PRODUCT, 4L).label());
        assertEquals("Socolait", resolver.resolve(ReferenceTarget.SUPPLIER, 5L).label());
    }

    @Test
    void shouldThrowBusiness_whenReferenceDoesNotExist() {
        when(venteClient.findCustomer(99L)).thenReturn(Optional.empty());

        BusinessException error = assertThrows(BusinessException.class,
                () -> resolver.resolve(ReferenceTarget.CUSTOMER, 99L));

        assertEquals("Référence introuvable : client n°99", error.getMessage());
    }
}
