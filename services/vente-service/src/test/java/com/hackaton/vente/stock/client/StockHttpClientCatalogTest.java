package com.hackaton.vente.stock.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.hackaton.vente.common.exception.ServiceUnavailableException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class StockHttpClientCatalogTest {

    private MockRestServiceServer server;
    private StockHttpClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new StockHttpClient(builder, "http://stock");
    }

    @Test
    void shouldEncodeSearchAndReadProducts_whenStockAnswers() {
        server.expect(requestTo("http://stock/products?search=riz%20%26%20sel"))
                .andRespond(withSuccess("[{\"id\":10,\"name\":\"Riz blanc\",\"sku\":\"RIZ-50\",\"unit\":\"sac\","
                        + "\"unitPrice\":93000,\"quantity\":40,\"lowStock\":false}]", MediaType.APPLICATION_JSON));

        List<StockCatalogProduct> products = client.searchProducts(" riz & sel ");

        assertEquals(1, products.size());
        assertEquals("RIZ-50", products.get(0).sku());
        server.verify();
    }

    @Test
    void shouldThrowUnavailable_whenStockFails() {
        server.expect(requestTo("http://stock/products?search=")).andRespond(withServerError());

        assertThrows(ServiceUnavailableException.class, () -> client.searchProducts(null));
    }
}
