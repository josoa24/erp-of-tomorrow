package com.hackaton.stock.movement.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.common.exception.InvalidRequestException;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.service.creator.StockMovementCreator;
import com.hackaton.stock.movement.service.reader.StockMovementReader;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(StockMovementController.class)
class StockMovementControllerLotTest {

    private static final String ENTRY_WITH_LOT = """
            {"type":"IN","quantity":48,"reason":"Livraison","lotNumber":"LC-2611","expiryDate":"2026-12-15"}""";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockMovementCreator movementCreator;
    @MockitoBean
    private StockMovementReader movementReader;

    private ResultActions record(String body) throws Exception {
        return mockMvc.perform(post("/products/3/movements")
                .headers(admin()).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void shouldBindLotNumberAndExpiryDate_whenEntryCarriesLot() throws Exception {
        when(movementCreator.record(eq(3L), any(MovementRequest.class))).thenReturn(
                new MovementResponse(7L, 3L, MovementType.IN, 48, "Livraison", 52, Instant.now()));

        record(ENTRY_WITH_LOT).andExpect(status().isCreated());

        ArgumentCaptor<MovementRequest> captor = ArgumentCaptor.forClass(MovementRequest.class);
        verify(movementCreator).record(eq(3L), captor.capture());
        assertEquals("LC-2611", captor.getValue().lotNumber());
        assertEquals(LocalDate.of(2026, 12, 15), captor.getValue().expiryDate());
    }

    @Test
    void shouldReturn400_whenLotNumberIsLongerThan64Characters() throws Exception {
        record("""
                {"type":"IN","quantity":1,"lotNumber":"%s","expiryDate":"2026-12-15"}""".formatted("L".repeat(65)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenLotRulesAreBroken() throws Exception {
        when(movementCreator.record(eq(3L), any(MovementRequest.class)))
                .thenThrow(new InvalidRequestException("Un lot ne se renseigne que sur une entrée de stock"));

        record(ENTRY_WITH_LOT)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn409_whenLotNumberAlreadyExists() throws Exception {
        when(movementCreator.record(eq(3L), any(MovementRequest.class)))
                .thenThrow(new DuplicateResourceException("Le lot LC-2611 existe déjà pour Lait concentré"));

        record(ENTRY_WITH_LOT)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
