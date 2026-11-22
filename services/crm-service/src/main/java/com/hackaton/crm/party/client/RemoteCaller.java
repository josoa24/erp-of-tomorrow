package com.hackaton.crm.party.client;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.common.exception.ResourceNotFoundException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

public final class RemoteCaller {

    private RemoteCaller() {
    }

    public static <T> T call(String serviceName, String resource, Long id, Supplier<T> action) {
        try {
            return action.get();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException(resource, id);
        } catch (HttpClientErrorException ex) {
            throw new BusinessException(refusedBy(serviceName));
        } catch (RestClientException ex) {
            throw unavailable(serviceName);
        }
    }

    public static <T> T fetch(String serviceName, Supplier<T> action) {
        try {
            return action.get();
        } catch (HttpClientErrorException ex) {
            throw new BusinessException(refusedBy(serviceName));
        } catch (RestClientException ex) {
            throw unavailable(serviceName);
        }
    }

    public static <T> List<T> fetchList(String serviceName, Supplier<List<T>> action) {
        return Optional.ofNullable(fetch(serviceName, action)).orElse(List.of());
    }

    public static <T> T send(String serviceName, Supplier<T> action) {
        try {
            return action.get();
        } catch (HttpClientErrorException ex) {
            throw new BusinessException(remoteMessage(ex, refusedBy(serviceName)));
        } catch (RestClientException ex) {
            throw unavailable(serviceName);
        }
    }

    private static String remoteMessage(HttpClientErrorException ex, String fallback) {
        try {
            RemoteError error = ex.getResponseBodyAs(RemoteError.class);
            return error == null || error.message() == null || error.message().isBlank() ? fallback : error.message();
        } catch (RuntimeException unreadable) {
            return fallback;
        }
    }

    private static String refusedBy(String serviceName) {
        return "Requête refusée par le service " + serviceName;
    }

    private static ServiceUnavailableException unavailable(String serviceName) {
        return new ServiceUnavailableException("Le service " + serviceName + " est indisponible");
    }
}
