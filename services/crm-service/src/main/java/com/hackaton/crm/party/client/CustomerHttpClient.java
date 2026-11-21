package com.hackaton.crm.party.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CustomerHttpClient implements CustomerClient {

    private static final String SERVICE = "Vente";

    private final RestClient restClient;

    public CustomerHttpClient(RestClient.Builder builder, @Value("${remote.vente-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public PartyInfo findById(Long customerId) {
        return RemoteCaller.call(SERVICE, "Client", customerId, () -> restClient.get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .body(PartyInfo.class));
    }

    @Override
    public List<RemoteSale> findSales(Long customerId) {
        return RemoteCaller.call(SERVICE, "Client", customerId, () -> restClient.get()
                .uri("/sales?customerId={id}", customerId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RemoteSale>>() { }));
    }
}
