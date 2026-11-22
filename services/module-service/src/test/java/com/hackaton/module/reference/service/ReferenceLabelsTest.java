package com.hackaton.module.reference.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.module.reference.client.RemoteEmployee;
import org.junit.jupiter.api.Test;

class ReferenceLabelsTest {

    @Test
    void shouldPadSaleNumberAndAppendCustomer() {
        assertEquals("V-0012 · Hery Rabe", ReferenceLabels.sale(12L, " Hery Rabe "));
    }

    @Test
    void shouldKeepOnlySaleNumber_whenCustomerIsUnknown() {
        assertEquals("V-0012", ReferenceLabels.sale(12L, null));
        assertEquals("V-12345", ReferenceLabels.sale(12345L, " "));
    }

    @Test
    void shouldJoinEmployeeNames_skippingMissingParts() {
        assertEquals("Toky Rakoto", ReferenceLabels.employee(new RemoteEmployee(3L, "Toky", "Rakoto")));
        assertEquals("Rasoa", ReferenceLabels.employee(new RemoteEmployee(4L, " Rasoa ", null)));
    }
}
