package com.hackaton.crm.party.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CustomerHttpClientTest {

    private MockRestServiceServer server;
    private CustomerHttpClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new CustomerHttpClient(builder, "http://vente");
    }

    @Test
    void shouldReadCompanyFields_whenListingCustomers() {
        server.expect(requestTo("http://vente/customers")).andRespond(withSuccess("""
                [{"id":1,"name":"Hotely Soa","type":"ENTREPRISE","nif":"4000123456","creditLimit":2000000,\
                "createdAt":"2026-11-01T08:00:00Z","notes":"ignoré"}]""", MediaType.APPLICATION_JSON));

        RemoteCustomer customer = client.findAll().get(0);

        assertEquals("ENTREPRISE", customer.type());
        assertEquals(new BigDecimal("2000000"), customer.creditLimit());
    }

    @Test
    void shouldAskForValidatedSalesFromTheGivenDay() {
        server.expect(requestTo("http://vente/sales?status=VALIDATED&from=2026-09-30"))
                .andRespond(withSuccess("[{\"customerId\":3,\"validatedAt\":\"2026-10-02T08:00:00Z\"}]",
                        MediaType.APPLICATION_JSON));

        assertEquals(3L, client.findValidatedSalesSince(LocalDate.of(2026, 9, 30)).get(0).customerId());
    }

    @Test
    void shouldPostTheCustomer_whenCreatingIt() {
        server.expect(requestTo("http://vente/customers")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"name\":\"Hotely Soa\",\"type\":\"ENTREPRISE\",\"nif\":\"4000123456\"}"))
                .andRespond(withSuccess("{\"id\":9,\"name\":\"Hotely Soa\"}", MediaType.APPLICATION_JSON));

        RemoteCustomer created = client.create(
                new NewCustomer("Hotely Soa", null, null, null, "ENTREPRISE", "4000123456", null, null));

        assertEquals(9L, created.id());
    }

    @Test
    void shouldRelayTheVenteMessage_whenCreationIsRefused() {
        server.expect(requestTo("http://vente/customers")).andRespond(withStatus(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON).body("{\"type\":\"VALIDATION\",\"message\":\"email : invalide\"}"));

        BusinessException error = assertThrows(BusinessException.class,
                () -> client.create(new NewCustomer("Soa", null, "x", null, null, null, null, null)));

        assertEquals("email : invalide", error.getMessage());
    }
}
