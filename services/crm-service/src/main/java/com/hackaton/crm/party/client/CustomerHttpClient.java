package com.hackaton.crm.party.client;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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

    @Override
    public List<RemoteCustomer> findAll() {
        return RemoteCaller.fetchList(SERVICE, () -> restClient.get()
                .uri("/customers")
                .retrieve()
                .body(new ParameterizedTypeReference<List<RemoteCustomer>>() { }));
    }

    @Override
    public RemoteCustomer create(NewCustomer customer) {
        return RemoteCaller.send(SERVICE, () -> restClient.post()
                .uri("/customers")
                .body(customer)
                .retrieve()
                .body(RemoteCustomer.class));
    }

    @Override
    public List<RemoteCredit> findCredits() {
        return RemoteCaller.fetchList(SERVICE, this::creditsOrNoneWhenEndpointIsMissing);
    }

    @Override
    public List<RemoteCustomerSale> findValidatedSalesSince(LocalDate from) {
        return RemoteCaller.fetchList(SERVICE, () -> restClient.get()
                .uri("/sales?status=VALIDATED&from={from}", from)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RemoteCustomerSale>>() { }));
    }

    private List<RemoteCredit> creditsOrNoneWhenEndpointIsMissing() {
        try {
            return restClient.get()
                    .uri("/sales/credit-by-customer")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RemoteCredit>>() { });
        } catch (HttpClientErrorException.NotFound | HttpClientErrorException.BadRequest missingEndpoint) {
            return List.of();
        }
    }
}
