package com.hackaton.crm.party.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class SupplierHttpClientTest {

    private MockRestServiceServer server;
    private SupplierHttpClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new SupplierHttpClient(builder, "http://stock");
    }

    @Test
    void shouldReadCityAndNif_whenListingSuppliers() {
        server.expect(requestTo("http://stock/suppliers")).andRespond(withSuccess(
                "[{\"id\":1,\"name\":\"Socolait\",\"city\":\"Antsirabe\",\"nif\":\"3000456789\",\"bankAccountLast4\":\"1234\"}]",
                MediaType.APPLICATION_JSON));

        RemoteSupplier supplier = client.findAll().get(0);

        assertEquals("Antsirabe", supplier.city());
        assertEquals("3000456789", supplier.nif());
    }

    @Test
    void shouldFilterPurchaseOrdersByStatus() {
        server.expect(requestTo("http://stock/purchase-orders?status=VALIDATED"))
                .andRespond(withSuccess("[{\"id\":4,\"status\":\"VALIDATED\"}]", MediaType.APPLICATION_JSON));

        assertEquals(1, client.findOrdersByStatus("VALIDATED").size());
    }

    @Test
    void shouldFailAsUnavailable_whenStockAnswersWithAServerError() {
        server.expect(requestTo("http://stock/suppliers")).andRespond(withServerError());

        assertThrows(ServiceUnavailableException.class, () -> client.findAll());
    }

    @Test
    void shouldPostTheSupplier_whenCreatingIt() {
        server.expect(requestTo("http://stock/suppliers")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"name\":\"Socolait\",\"city\":\"Antsirabe\"}"))
                .andRespond(withSuccess("{\"id\":5,\"name\":\"Socolait\"}", MediaType.APPLICATION_JSON));

        assertEquals(5L, client.create(new NewSupplier("Socolait", null, null, "Antsirabe", null, null)).id());
    }

    @Test
    void shouldRelayTheStockMessage_whenSupplierAlreadyExists() {
        server.expect(requestTo("http://stock/suppliers")).andRespond(withStatus(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"BUSINESS\",\"message\":\"Le fournisseur Socolait existe déjà\"}"));

        BusinessException error = assertThrows(BusinessException.class,
                () -> client.create(new NewSupplier("Socolait", null, null, null, null, null)));

        assertEquals("Le fournisseur Socolait existe déjà", error.getMessage());
    }
}
