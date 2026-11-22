package com.hackaton.module.reference.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hackaton.module.common.exception.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class VenteHttpClientTest {

    private MockRestServiceServer server;
    private VenteHttpClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new VenteHttpClient(builder, "http://vente");
    }

    @Test
    void shouldReadCustomerName_whenCustomerExists() {
        server.expect(requestTo("http://vente/customers/7")).andRespond(withSuccess(
                "{\"id\":7,\"name\":\"Hery Rabe\",\"city\":\"Antananarivo\"}", MediaType.APPLICATION_JSON));

        assertEquals("Hery Rabe", client.findCustomer(7L).orElseThrow().name());
    }

    @Test
    void shouldReturnEmpty_whenSaleDoesNotExist() {
        server.expect(requestTo("http://vente/sales/12")).andRespond(withResourceNotFound());

        assertTrue(client.findSale(12L).isEmpty());
    }

    @Test
    void shouldAskValidatedSalesToDeliver_withinDates() {
        server.expect(requestTo("http://vente/sales?status=VALIDATED&from=2026-11-15&to=2026-11-22&toDeliver=true"))
                .andRespond(withSuccess("[{\"id\":12,\"customerId\":7,\"status\":\"VALIDATED\",\"totalAmount\":45000,"
                        + "\"createdAt\":\"2026-11-18T07:00:00Z\",\"toDeliver\":true}]", MediaType.APPLICATION_JSON));

        List<RemoteSale> sales = client.findValidatedSalesToDeliver(LocalDate.parse("2026-11-15"),
                LocalDate.parse("2026-11-22"));

        assertEquals(1, sales.size());
        assertEquals(new BigDecimal("45000"), sales.get(0).totalAmount());
        assertEquals(Boolean.TRUE, sales.get(0).toDeliver());
    }

    @Test
    void shouldThrowUnavailable_whenVenteFails() {
        server.expect(requestTo("http://vente/customers/7")).andRespond(withServerError());

        assertThrows(ServiceUnavailableException.class, () -> client.findCustomer(7L));
    }
}
