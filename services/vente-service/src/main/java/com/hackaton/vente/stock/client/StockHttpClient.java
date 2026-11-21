package com.hackaton.vente.stock.client;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ServiceUnavailableException;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class StockHttpClient implements StockClient {

    private static final Pattern MESSAGE = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]*)\"");

    private final RestClient restClient;

    public StockHttpClient(RestClient.Builder builder, @Value("${stock.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public StockProduct getProduct(Long productId) {
        return call(() -> restClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .body(StockProduct.class));
    }

    @Override
    public void recordMovement(Long productId, MovementKind kind, int quantity, String reason) {
        call(() -> restClient.post()
                .uri("/products/{id}/movements", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("type", kind.name(), "quantity", quantity, "reason", reason))
                .retrieve()
                .toBodilessEntity());
    }

    private <T> T call(Supplier<T> action) {
        try {
            return action.get();
        } catch (HttpClientErrorException ex) {
            throw new BusinessException(extractMessage(ex));
        } catch (RestClientException ex) {
            throw new ServiceUnavailableException("Le service Stock est indisponible");
        }
    }

    private String extractMessage(HttpClientErrorException ex) {
        Matcher matcher = MESSAGE.matcher(ex.getResponseBodyAsString());
        return matcher.find() ? matcher.group(1) : "Requête refusée par le service Stock";
    }
}
