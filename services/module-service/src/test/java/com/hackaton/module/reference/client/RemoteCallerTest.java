package com.hackaton.module.reference.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.ServiceUnavailableException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

class RemoteCallerTest {

    @Test
    void shouldReturnValue_whenCallSucceeds() {
        assertEquals(Optional.of("ok"), RemoteCaller.find("Vente", () -> "ok"));
    }

    @Test
    void shouldReturnEmpty_whenRemoteAnswers404() {
        assertEquals(Optional.empty(), RemoteCaller.find("Vente", () -> {
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "nf", HttpHeaders.EMPTY, new byte[0], null);
        }));
    }

    @Test
    void shouldUseFallback_whenListIsMissing() {
        assertEquals(List.of(), RemoteCaller.call("Vente", () -> null, List.of()));
    }

    @Test
    void shouldTranslateOther4xxIntoBusinessError() {
        assertThrows(BusinessException.class, () -> RemoteCaller.find("RH", () -> {
            throw HttpClientErrorException.create(HttpStatus.FORBIDDEN, "no", HttpHeaders.EMPTY, new byte[0], null);
        }));
    }

    @Test
    void shouldTranslateConnectionFailureAnd5xxIntoUnavailable() {
        ServiceUnavailableException down = assertThrows(ServiceUnavailableException.class,
                () -> RemoteCaller.find("Stock", () -> {
                    throw new ResourceAccessException("connexion refusée");
                }));

        assertEquals("Le service Stock est indisponible", down.getMessage());
        assertThrows(ServiceUnavailableException.class, () -> RemoteCaller.find("Stock", () -> {
            throw HttpServerErrorException.create(HttpStatus.BAD_GATEWAY, "bg", HttpHeaders.EMPTY, new byte[0], null);
        }));
    }
}
