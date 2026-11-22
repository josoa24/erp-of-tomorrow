package com.hackaton.module;

import static com.hackaton.module.support.GatewayHeaders.asAdmin;
import static com.hackaton.module.support.GatewayHeaders.asSeller;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.module.reference.client.RemoteNamed;
import com.hackaton.module.reference.client.VenteClient;
import com.hackaton.module.support.RequestFixtures;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class ModuleServiceFlowTest {

    private static final String RECORDS = "/definitions/livraisons/records";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private VenteClient venteClient;

    @Test
    void shouldCreateModuleRecordAdvanceAndCount_throughTheWholeService() throws Exception {
        when(venteClient.findCustomer(7L)).thenReturn(Optional.of(new RemoteNamed(7L, "Hery Rabe")));

        mockMvc.perform(asSeller(post("/definitions")).contentType(MediaType.APPLICATION_JSON).content(definition()))
                .andExpect(status().isForbidden());
        mockMvc.perform(asAdmin(post("/definitions")).contentType(MediaType.APPLICATION_JSON).content(definition()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("livraisons"));
        mockMvc.perform(asSeller(post(RECORDS)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\":{\"client\":{\"id\":7},\"montant\":25000,\"mode\":\"Moto\"}}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.values.client.label").value("Hery Rabe"))
                .andExpect(jsonPath("$.status").value("a_preparer"));

        mockMvc.perform(asSeller(get(RECORDS).param("search", "hery")))
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$[0].values.montant").value(25000));
        mockMvc.perform(asSeller(get("/definitions/livraisons/indicators")))
                .andExpect(jsonPath("$[1].value").value(25000))
                .andExpect(jsonPath("$[1].money").value(true));
        mockMvc.perform(asSeller(get("/definitions/livraisons/status-counts")))
                .andExpect(jsonPath("$.byStatus.a_preparer").value(1));
        mockMvc.perform(asAdmin(post("/definitions/livraisons/sales-import")))
                .andExpect(status().isUnprocessableEntity());
        mockMvc.perform(asAdmin(post("/definitions")).contentType(MediaType.APPLICATION_JSON).content(definition()))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldActivateAdaptationOnce_andListIt() throws Exception {
        String body = "{\"moduleKey\":\"equipe\",\"adaptationKey\":\"equipe.avances-salaire\"}";

        mockMvc.perform(asAdmin(post("/adaptations")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(asAdmin(post("/adaptations")).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
        mockMvc.perform(asSeller(get("/adaptations")))
                .andExpect(jsonPath("$[0].activatedBy").value("hery.rabe@nova.mg"));
    }

    private String definition() throws Exception {
        return objectMapper.writeValueAsString(RequestFixtures.livraisons());
    }
}
