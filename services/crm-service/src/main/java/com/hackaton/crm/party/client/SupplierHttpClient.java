package com.hackaton.crm.party.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SupplierHttpClient implements SupplierClient {

    private static final String SERVICE = "Stock";

    private final RestClient restClient;

    public SupplierHttpClient(RestClient.Builder builder, @Value("${remote.stock-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public PartyInfo findById(Long supplierId) {
        return RemoteCaller.call(SERVICE, "Fournisseur", supplierId, () -> restClient.get()
                .uri("/suppliers/{id}", supplierId)
                .retrieve()
                .body(PartyInfo.class));
    }

    @Override
    public List<RemoteOrder> findOrders(Long supplierId) {
        return RemoteCaller.call(SERVICE, "Fournisseur", supplierId, () -> restClient.get()
                .uri("/purchase-orders?supplierId={id}", supplierId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RemoteOrder>>() { }));
    }
}
