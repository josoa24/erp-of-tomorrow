package com.hackaton.module.reference.client;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RhHttpClient implements RhClient {

    private static final String SERVICE = "RH";

    private final RestClient restClient;

    public RhHttpClient(RestClient.Builder builder, @Value("${remote.rh-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<RemoteEmployee> findEmployee(Long id) {
        return RemoteCaller.find(SERVICE, () -> restClient.get().uri("/employees/{id}", id)
                .retrieve().body(RemoteEmployee.class));
    }
}
