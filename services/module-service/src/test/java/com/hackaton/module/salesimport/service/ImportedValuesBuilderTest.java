package com.hackaton.module.salesimport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.module.reference.client.RemoteNamed;
import com.hackaton.module.reference.client.RemoteSale;
import com.hackaton.module.reference.client.VenteClient;
import com.hackaton.module.support.RecordFixtures;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportedValuesBuilderTest {

    private static final ImportColumns COLUMNS = new ImportColumns("vente", "client", "montant");

    @Mock
    private VenteClient venteClient;

    @Test
    void shouldFillSaleCustomerAndAmount_andLookUpEachCustomerOnce() {
        when(venteClient.findCustomer(7L)).thenReturn(Optional.of(new RemoteNamed(7L, "Hery Rabe")));
        ImportedValuesBuilder builder = new ImportedValuesBuilder(venteClient);
        Map<Long, Optional<String>> names = new HashMap<>();

        Map<String, Object> first = builder.build(sale(12L, 7L), COLUMNS, names);
        builder.build(sale(13L, 7L), COLUMNS, names);

        assertEquals(RecordFixtures.ref(12, "V-0012 · Hery Rabe"), first.get("vente"));
        assertEquals(RecordFixtures.ref(7, "Hery Rabe"), first.get("client"));
        assertEquals(new BigDecimal("45000"), first.get("montant"));
        verify(venteClient, times(1)).findCustomer(7L);
    }

    @Test
    void shouldFillOnlySale_whenCustomerIsUnknownAndColumnsAreAbsent() {
        when(venteClient.findCustomer(8L)).thenReturn(Optional.empty());
        ImportedValuesBuilder builder = new ImportedValuesBuilder(venteClient);

        Map<String, Object> values = builder.build(sale(14L, 8L), new ImportColumns("vente", "client", null),
                new HashMap<>());

        assertEquals(RecordFixtures.ref(14, "V-0014"), values.get("vente"));
        assertFalse(values.containsKey("client"));
        assertEquals(1, values.size());
    }

    private RemoteSale sale(long id, Long customerId) {
        return new RemoteSale(id, customerId, "VALIDATED", new BigDecimal("45000"), null, null, true);
    }
}
