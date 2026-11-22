package com.hackaton.stock.movement.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.InsufficientStockException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.service.creator.StockMovementCreator;
import com.hackaton.stock.movement.service.reader.StockMovementReader;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(StockMovementController.class)
class StockMovementControllerWriteTest {

    private static final String OUT_BODY = """
            {"type":"OUT","quantity":4,"reason":"Vente"}""";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockMovementCreator movementCreator;
    @MockitoBean
    private StockMovementReader movementReader;

    private MockHttpServletRequestBuilder recordRequest(long productId, String body) {
        return post("/products/" + productId + "/movements")
                .headers(admin()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void shouldReturn201_whenMovementIsRecorded() throws Exception {
        when(movementCreator.record(eq(1L), any(MovementRequest.class))).thenReturn(
                new MovementResponse(6L, 1L, MovementType.OUT, 4, "Vente", 16, Instant.now()));

        mockMvc.perform(recordRequest(1L, OUT_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.stockAfter").value(16));
    }

    @Test
    void shouldReturn400_whenQuantityIsNotPositive() throws Exception {
        mockMvc.perform(recordRequest(1L, """
                        {"type":"IN","quantity":0}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenTypeIsUnknown() throws Exception {
        mockMvc.perform(recordRequest(1L, """
                        {"type":"TRANSFER","quantity":3}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenProductDoesNotExist() throws Exception {
        when(movementCreator.record(eq(9L), any(MovementRequest.class)))
                .thenThrow(new ResourceNotFoundException("Produit", 9L));

        mockMvc.perform(recordRequest(9L, OUT_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn422_whenStockIsInsufficient() throws Exception {
        when(movementCreator.record(eq(1L), any(MovementRequest.class)))
                .thenThrow(new InsufficientStockException("Riz", 2, 4));

        mockMvc.perform(recordRequest(1L, OUT_BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
