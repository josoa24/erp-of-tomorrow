package com.hackaton.module.insight.controller;

import static com.hackaton.module.support.GatewayHeaders.asSeller;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.definition.entity.IndicatorKind;
import com.hackaton.module.insight.dto.IndicatorValueResponse;
import com.hackaton.module.insight.dto.StatusCountsResponse;
import com.hackaton.module.insight.service.IndicatorReader;
import com.hackaton.module.insight.service.StatusCountReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ModuleInsightController.class)
class ModuleInsightControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private IndicatorReader indicatorReader;
    @MockitoBean
    private StatusCountReader statusCountReader;

    @Test
    void shouldReturnIndicators_withValueTotalAndMoneyFlag() throws Exception {
        when(indicatorReader.compute("livraisons")).thenReturn(List.of(new IndicatorValueResponse("livrees_jour",
                "Livrées aujourd'hui", "truck", IndicatorKind.RATIO_TODAY, BigDecimal.valueOf(4), 6L, false, List.of())));

        mockMvc.perform(asSeller(get("/definitions/livraisons/indicators")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(4))
                .andExpect(jsonPath("$[0].total").value(6))
                .andExpect(jsonPath("$[0].money").value(false));
    }

    @Test
    void shouldReturnStatusCounts() throws Exception {
        when(statusCountReader.count("livraisons"))
                .thenReturn(new StatusCountsResponse(5, Map.of("a_preparer", 3L, "livree", 2L)));

        mockMvc.perform(asSeller(get("/definitions/livraisons/status-counts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(5))
                .andExpect(jsonPath("$.byStatus.a_preparer").value(3));
    }

    @Test
    void shouldReturn404_whenModuleIsUnknown() throws Exception {
        when(indicatorReader.compute("inconnu")).thenThrow(new ResourceNotFoundException("Module introuvable"));

        mockMvc.perform(asSeller(get("/definitions/inconnu/indicators")))
                .andExpect(status().isNotFound());
    }
}
