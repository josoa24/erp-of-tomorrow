package com.hackaton.module.reference.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hackaton.module.common.security.IdentityForwarding;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class RhStockHttpClientTest {

    @AfterEach
    void clearRequest() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldForwardCallerHeaders_whenReadingAnEmployee() {
        MockHttpServletRequest incoming = new MockHttpServletRequest();
        incoming.addHeader("X-User-Id", "2");
        incoming.addHeader("X-User-Role", "VENTE");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(incoming));
        RestClient.Builder builder = RestClient.builder();
        new IdentityForwarding().customize(builder);
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://rh/employees/3")).andExpect(header("X-User-Role", "VENTE"))
                .andRespond(withSuccess("{\"id\":3,\"firstName\":\"Toky\",\"lastName\":\"Rakoto\"}",
                        MediaType.APPLICATION_JSON));

        RemoteEmployee employee = new RhHttpClient(builder, "http://rh").findEmployee(3L).orElseThrow();

        assertEquals("Toky", employee.firstName());
        server.verify();
    }

    @Test
    void shouldReadProductAndSupplierNames() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("http://stock/products/4"))
                .andRespond(withSuccess("{\"id\":4,\"name\":\"Riz blanc\"}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://stock/suppliers/9")).andRespond(withResourceNotFound());
        StockHttpClient client = new StockHttpClient(builder, "http://stock");

        assertEquals("Riz blanc", client.findProduct(4L).orElseThrow().name());
        assertTrue(client.findSupplier(9L).isEmpty());
    }
}
