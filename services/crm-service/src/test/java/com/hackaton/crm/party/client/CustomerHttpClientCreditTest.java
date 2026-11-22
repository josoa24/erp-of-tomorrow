package com.hackaton.crm.party.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hackaton.crm.common.exception.ServiceUnavailableException;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CustomerHttpClientCreditTest {

    private static final String CREDITS = "http://vente/sales/credit-by-customer";

    private MockRestServiceServer server;
    private CustomerHttpClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new CustomerHttpClient(builder, "http://vente");
    }

    @Test
    void shouldReadOutstandingCredit_whenVenteExposesIt() {
        server.expect(requestTo(CREDITS)).andRespond(withSuccess(
                "[{\"customerId\":1,\"outstanding\":1800000,\"overdueCount\":1}]", MediaType.APPLICATION_JSON));

        assertEquals(new BigDecimal("1800000"), client.findCredits().get(0).outstanding());
    }

    @Test
    void shouldReturnNoCredit_whenVenteDoesNotExposeTheEndpointYet() {
        server.expect(requestTo(CREDITS)).andRespond(withResourceNotFound());

        assertTrue(client.findCredits().isEmpty());
    }

    @Test
    void shouldReturnNoCredit_whenVenteReadsThePathAsASaleId() {
        server.expect(requestTo(CREDITS)).andRespond(withBadRequest());

        assertTrue(client.findCredits().isEmpty());
    }

    @Test
    void shouldFailAsUnavailable_whenVenteAnswersWithAServerError() {
        server.expect(requestTo(CREDITS)).andRespond(withServerError());

        assertThrows(ServiceUnavailableException.class, () -> client.findCredits());
    }
}
