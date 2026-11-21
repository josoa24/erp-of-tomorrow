package com.hackaton.crm.party.client;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.common.exception.ResourceNotFoundException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
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
            throw new BusinessException("Requête refusée par le service " + serviceName);
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Le service " + serviceName + " est indisponible");
        }
    }
}
