package com.hackaton.stock.supplier.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.entity.Supplier;
import org.junit.jupiter.api.Test;

class SupplierMapperTest {

    private final SupplierMapper mapper = new SupplierMapper();

    private SupplierRequest request(String city, String nif, String stat) {
        return new SupplierRequest("Socolait", "0331234567", null, "Lot II M 45", null, 3, null, city, nif, stat);
    }

    @Test
    void shouldCopyCityNifAndStat_whenTheyAreGiven() {
        Supplier supplier = mapper.toEntity(request(" Antsirabe ", "3000456789", "46101 11 2019 0 00456"));

        SupplierResponse response = mapper.toResponse(supplier);

        assertEquals("Antsirabe", response.city());
        assertEquals("3000456789", response.nif());
        assertEquals("46101 11 2019 0 00456", response.stat());
    }

    @Test
    void shouldStoreNull_whenCityNifAndStatAreBlank() {
        Supplier supplier = mapper.toEntity(request(" ", "", null));

        assertNull(supplier.getCity());
        assertNull(supplier.getNif());
        assertNull(supplier.getStat());
    }

    @Test
    void shouldClearTheCity_whenUpdateOmitsIt() {
        Supplier supplier = mapper.toEntity(request("Toamasina", null, null));

        mapper.apply(supplier, request(null, null, null));

        assertNull(supplier.getCity());
    }
}
