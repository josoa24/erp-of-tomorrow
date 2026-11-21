package com.hackaton.crm.party.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.common.exception.ResourceNotFoundException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

class RemoteCallerTest {

    @Test
    void shouldReturnTheValue_whenTheCallSucceeds() {
        assertEquals("ok", RemoteCaller.call("Vente", "Client", 1L, () -> "ok"));
    }

    @Test
    void shouldTranslate404IntoNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> RemoteCaller.call("Vente", "Client", 1L, () -> {
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "nf", HttpHeaders.EMPTY, new byte[0], null);
        }));
    }

    @Test
    void shouldTranslateOther4xxIntoBusinessError() {
        assertThrows(BusinessException.class, () -> RemoteCaller.call("Stock", "Fournisseur", 1L, () -> {
            throw HttpClientErrorException.create(HttpStatus.FORBIDDEN, "no", HttpHeaders.EMPTY, new byte[0], null);
        }));
    }

    @Test
    void shouldTranslateConnectionFailureIntoUnavailable() {
        ServiceUnavailableException error = assertThrows(ServiceUnavailableException.class,
                () -> RemoteCaller.call("Vente", "Client", 1L, () -> {
                    throw new ResourceAccessException("connexion refusee");
                }));

        assertEquals("Le service Vente est indisponible", error.getMessage());
    }
}
