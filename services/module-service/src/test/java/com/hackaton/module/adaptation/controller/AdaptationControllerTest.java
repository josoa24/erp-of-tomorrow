package com.hackaton.module.adaptation.controller;

import static com.hackaton.module.support.GatewayHeaders.asAdmin;
import static com.hackaton.module.support.GatewayHeaders.asSeller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.module.adaptation.dto.AdaptationRequest;
import com.hackaton.module.adaptation.dto.AdaptationResponse;
import com.hackaton.module.adaptation.service.creator.AdaptationCreator;
import com.hackaton.module.adaptation.service.deleter.AdaptationDeleter;
import com.hackaton.module.adaptation.service.reader.AdaptationReader;
import com.hackaton.module.common.exception.ConflictException;
import com.hackaton.module.common.exception.ForbiddenException;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.support.TestClocks;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdaptationController.class)
class AdaptationControllerTest {

    private static final String BODY = "{\"moduleKey\":\"stock\",\"adaptationKey\":\"stock.lots-peremption\"}";
    private static final AdaptationResponse ACTIVE =
            new AdaptationResponse("stock", "stock.lots-peremption", "hery.rabe@nova.mg", TestClocks.NOW);

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AdaptationReader adaptationReader;
    @MockitoBean
    private AdaptationCreator adaptationCreator;
    @MockitoBean
    private AdaptationDeleter adaptationDeleter;

    @Test
    void shouldListActivations_forAnyRole() throws Exception {
        when(adaptationReader.findAll()).thenReturn(List.of(ACTIVE));

        mockMvc.perform(asSeller(get("/adaptations")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adaptationKey").value("stock.lots-peremption"));
    }

    @Test
    void shouldReturn201_whenAdminActivates() throws Exception {
        when(adaptationCreator.activate(eq(new AdaptationRequest("stock", "stock.lots-peremption")), any()))
                .thenReturn(ACTIVE);

        mockMvc.perform(asAdmin(post("/adaptations")).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activatedBy").value("hery.rabe@nova.mg"));
    }

    @Test
    void shouldReturn403And409_whenRefused() throws Exception {
        when(adaptationCreator.activate(any(), any())).thenThrow(new ForbiddenException("Réservé"))
                .thenThrow(new ConflictException("Déjà active"));

        mockMvc.perform(asSeller(post("/adaptations")).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
        mockMvc.perform(asAdmin(post("/adaptations")).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    @Test
    void shouldReturn400_whenKeysAreMissing() throws Exception {
        mockMvc.perform(asAdmin(post("/adaptations")).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(adaptationCreator);
    }

    @Test
    void shouldReturn204Or404_whenDeactivating() throws Exception {
        doThrow(new ResourceNotFoundException("Adaptation non active")).when(adaptationDeleter)
                .deactivate(eq("equipe.avances-salaire"), any());

        mockMvc.perform(asAdmin(delete("/adaptations/stock.lots-peremption"))).andExpect(status().isNoContent());
        mockMvc.perform(asAdmin(delete("/adaptations/equipe.avances-salaire"))).andExpect(status().isNotFound());
    }
}
