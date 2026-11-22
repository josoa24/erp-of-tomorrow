package com.hackaton.module.record.controller;

import static com.hackaton.module.support.GatewayHeaders.asSeller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.ImmutableResourceException;
import com.hackaton.module.common.exception.ServiceUnavailableException;
import com.hackaton.module.record.dto.RecordPage;
import com.hackaton.module.record.dto.RecordQuery;
import com.hackaton.module.record.service.creator.ModuleRecordCreator;
import com.hackaton.module.record.service.reader.ModuleRecordReader;
import com.hackaton.module.record.service.updater.ModuleRecordUpdater;
import com.hackaton.module.support.RecordFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ModuleRecordController.class)
class ModuleRecordControllerTest {

    private static final String URL = "/definitions/livraisons/records";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ModuleRecordReader recordReader;
    @MockitoBean
    private ModuleRecordCreator recordCreator;
    @MockitoBean
    private ModuleRecordUpdater recordUpdater;

    @Test
    void shouldReturnPageWithTotalHeader_andDefaultPagination() throws Exception {
        when(recordReader.search("livraisons", new RecordQuery("hery", null, 0, 20)))
                .thenReturn(new RecordPage(List.of(RecordFixtures.response(5, "a_preparer")), 42));

        mockMvc.perform(asSeller(get(URL).param("search", "hery")))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "42"))
                .andExpect(jsonPath("$[0].values.quartier").value("Isotry"));
    }

    @Test
    void shouldReturn201_whenAnyRoleCreatesARecord() throws Exception {
        when(recordCreator.create(eq("livraisons"), any(), argThat(caller -> !caller.isAdmin())))
                .thenReturn(RecordFixtures.response(5, "a_preparer"));

        mockMvc.perform(asSeller(post(URL)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\":{\"client\":{\"id\":7}}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("a_preparer"));
    }

    @Test
    void shouldReturn400_whenValuesAreMissing() throws Exception {
        mockMvc.perform(asSeller(post(URL)).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn422And503_whenReferenceCannotBeChecked() throws Exception {
        when(recordCreator.create(any(), any(), any()))
                .thenThrow(new BusinessException("Référence introuvable : client n°7"))
                .thenThrow(new ServiceUnavailableException("Le service Vente est indisponible"));
        String body = "{\"values\":{\"client\":{\"id\":7}}}";

        mockMvc.perform(asSeller(post(URL)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(asSeller(post(URL)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void shouldReturn200OnAdvance_and409WhenFinal() throws Exception {
        when(recordUpdater.advance("livraisons", 5L)).thenReturn(RecordFixtures.response(5, "en_route"))
                .thenThrow(new ImmutableResourceException("Statut final"));

        mockMvc.perform(asSeller(post(URL + "/5/advance"))).andExpect(jsonPath("$.status").value("en_route"));
        mockMvc.perform(asSeller(post(URL + "/5/advance")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }
}
