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

    @Override
    public List<RemoteSupplier> findAll() {
        return RemoteCaller.fetchList(SERVICE, () -> restClient.get()
                .uri("/suppliers")
                .retrieve()
                .body(new ParameterizedTypeReference<List<RemoteSupplier>>() { }));
    }

    @Override
    public RemoteSupplier create(NewSupplier supplier) {
        return RemoteCaller.send(SERVICE, () -> restClient.post()
                .uri("/suppliers")
                .body(supplier)
                .retrieve()
                .body(RemoteSupplier.class));
    }

    @Override
    public List<RemoteOrder> findOrdersByStatus(String status) {
        return RemoteCaller.fetchList(SERVICE, () -> restClient.get()
                .uri("/purchase-orders?status={status}", status)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RemoteOrder>>() { }));
    }
}
