package com.hackaton.module.reference.client;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class StockHttpClient implements StockClient {

    private static final String SERVICE = "Stock";

    private final RestClient restClient;

    public StockHttpClient(RestClient.Builder builder, @Value("${remote.stock-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<RemoteNamed> findProduct(Long id) {
        return RemoteCaller.find(SERVICE, () -> restClient.get().uri("/products/{id}", id)
                .retrieve().body(RemoteNamed.class));
    }

    @Override
    public Optional<RemoteNamed> findSupplier(Long id) {
        return RemoteCaller.find(SERVICE, () -> restClient.get().uri("/suppliers/{id}", id)
                .retrieve().body(RemoteNamed.class));
    }
}
