package com.hackaton.stock.movement.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.service.creator.StockMovementCreator;
import com.hackaton.stock.movement.service.reader.StockMovementReader;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockMovementController.class)
class StockMovementControllerReadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockMovementCreator movementCreator;
    @MockitoBean
    private StockMovementReader movementReader;

    @Test
    void shouldReturnMovements_whenProductExists() throws Exception {
        when(movementReader.findByProductId(1L)).thenReturn(List.of(
                new MovementResponse(5L, 1L, MovementType.IN, 10, "Réception", 20, Instant.now())));

        mockMvc.perform(get("/products/1/movements").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("IN"))
                .andExpect(jsonPath("$[0].stockAfter").value(20));
    }

    @Test
    void shouldReturn404_whenProductDoesNotExist() throws Exception {
        when(movementReader.findByProductId(9L)).thenThrow(new ResourceNotFoundException("Produit", 9L));

        mockMvc.perform(get("/products/9/movements").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
