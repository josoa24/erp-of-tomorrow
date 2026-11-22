package com.hackaton.module.reference.client;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.ServiceUnavailableException;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

public final class RemoteCaller {

    private RemoteCaller() {
    }

    public static <T> Optional<T> find(String serviceName, Supplier<T> action) {
        try {
            return Optional.ofNullable(action.get());
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            throw new BusinessException("Requête refusée par le service " + serviceName);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Le service " + serviceName + " est indisponible");
        }
    }

    public static <T> T call(String serviceName, Supplier<T> action, T fallback) {
        return find(serviceName, action).orElse(fallback);
    }
}
