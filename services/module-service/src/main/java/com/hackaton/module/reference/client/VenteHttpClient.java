package com.hackaton.module.reference.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class VenteHttpClient implements VenteClient {

    private static final String SERVICE = "Vente";
    private static final ParameterizedTypeReference<List<RemoteSale>> SALES = new ParameterizedTypeReference<>() { };

    private final RestClient restClient;

    public VenteHttpClient(RestClient.Builder builder, @Value("${remote.vente-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<RemoteNamed> findCustomer(Long id) {
        return RemoteCaller.find(SERVICE, () -> restClient.get().uri("/customers/{id}", id)
                .retrieve().body(RemoteNamed.class));
    }

    @Override
    public Optional<RemoteSale> findSale(Long id) {
        return RemoteCaller.find(SERVICE, () -> restClient.get().uri("/sales/{id}", id)
                .retrieve().body(RemoteSale.class));
    }

    @Override
    public List<RemoteSale> findValidatedSalesToDeliver(LocalDate from, LocalDate to) {
        return RemoteCaller.call(SERVICE, () -> restClient.get()
                .uri("/sales?status=VALIDATED&from={from}&to={to}&toDeliver=true", from, to)
                .retrieve().body(SALES), List.of());
    }
}
