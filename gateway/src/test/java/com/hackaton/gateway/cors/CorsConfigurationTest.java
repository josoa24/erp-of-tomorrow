package com.hackaton.gateway.cors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CorsConfigurationTest {

    private static final String APP_ORIGIN = "http://localhost:19006";

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @BeforeEach
    void connectToRunningGateway() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void shouldAcceptPreflight_whenOriginIsLocalApp() {
        client.options().uri("/api/stock/products")
                .header(HttpHeaders.ORIGIN, APP_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, APP_ORIGIN);
    }

    @Test
    void shouldRejectPreflight_whenOriginIsUnknown() {
        client.options().uri("/api/stock/products")
                .header(HttpHeaders.ORIGIN, "http://inconnu.example")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    @Test
    void shouldKeepCorsHeader_whenRequestIsUnauthorized() {
        client.get().uri("/api/stock/products")
                .header(HttpHeaders.ORIGIN, APP_ORIGIN)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, APP_ORIGIN);
    }
}
