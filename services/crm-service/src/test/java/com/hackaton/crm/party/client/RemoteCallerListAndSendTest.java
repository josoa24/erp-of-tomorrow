package com.hackaton.crm.party.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

class RemoteCallerListAndSendTest {

    private static HttpClientErrorException refused() {
        return HttpClientErrorException.create(HttpStatus.CONFLICT, "conflit", HttpHeaders.EMPTY, new byte[0], null);
    }

    @Test
    void shouldReturnAnEmptyList_whenTheRemoteBodyIsEmpty() {
        assertTrue(RemoteCaller.fetchList("Stock", () -> null).isEmpty());
    }

    @Test
    void shouldReturnTheList_whenTheRemoteAnswers() {
        assertEquals(List.of("Socolait"), RemoteCaller.fetchList("Stock", () -> List.of("Socolait")));
    }

    @Test
    void shouldTranslateA4xxIntoBusinessError_whenFetching() {
        assertThrows(BusinessException.class, () -> RemoteCaller.fetch("Vente", () -> {
            throw refused();
        }));
    }

    @Test
    void shouldTranslateConnectionFailureIntoUnavailable_whenFetching() {
        assertThrows(ServiceUnavailableException.class, () -> RemoteCaller.fetch("Vente", () -> {
            throw new ResourceAccessException("connexion refusee");
        }));
    }

    @Test
    void shouldFallBackToAGenericMessage_whenTheRefusalBodyIsUnreadable() {
        BusinessException error = assertThrows(BusinessException.class, () -> RemoteCaller.send("Stock", () -> {
            throw refused();
        }));

        assertEquals("Requête refusée par le service Stock", error.getMessage());
    }

    @Test
    void shouldTranslateConnectionFailureIntoUnavailable_whenSending() {
        assertThrows(ServiceUnavailableException.class, () -> RemoteCaller.send("Stock", () -> {
            throw new ResourceAccessException("connexion refusee");
        }));
    }
}
