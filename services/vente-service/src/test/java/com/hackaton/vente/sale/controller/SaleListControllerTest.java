package com.hackaton.vente.sale.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.sale.controller.SaleResponseFixtures.sale;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleCountsResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.service.counts.SaleCountsReader;
import com.hackaton.vente.sale.service.reader.SaleReader;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleListController.class)
class SaleListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleReader saleReader;

    @MockitoBean
    private SaleCountsReader countsReader;

    @Test
    void shouldReturnPageWithTotalHeader_whenFiltersAreValid() throws Exception {
        SaleSearchCriteria criteria = new SaleSearchCriteria(1L, DocumentStatus.VALIDATED,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30), "rova",
                List.of(PaymentStatus.TO_COLLECT, PaymentStatus.OVERDUE), PaymentMethod.CREDIT, true);
        when(saleReader.search(criteria, 0, 20))
                .thenReturn(new PageResult<>(List.of(sale(5L, DocumentStatus.VALIDATED)), 41));

        mockMvc.perform(get("/sales").param("customerId", "1").param("status", "VALIDATED")
                        .param("from", "2026-11-01").param("to", "2026-11-30").param("search", "rova")
                        .param("paymentStatus", "TO_COLLECT,OVERDUE").param("paymentMethod", "CREDIT")
                        .param("toDeliver", "true").param("page", "0").param("size", "20").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "41"))
                .andExpect(jsonPath("$[0].number").value("V-0005"))
                .andExpect(jsonPath("$[0].paymentStatus").value("TO_COLLECT"));
    }

    @Test
    void shouldReturnFullListWithTotal_whenNoPagingIsRequested() throws Exception {
        when(saleReader.search(any(SaleSearchCriteria.class), eq(null), eq(null)))
                .thenReturn(new PageResult<>(List.of(sale(5L, DocumentStatus.DRAFT)), 1));

        mockMvc.perform(get("/sales").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnCounts_whenRangeIsGiven() throws Exception {
        when(countsReader.count(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)))
                .thenReturn(new SaleCountsResponse(412, 9, 398, 5, 2, 8, 0));

        mockMvc.perform(get("/sales/counts").param("from", "2026-11-01").param("to", "2026-11-30").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(412))
                .andExpect(jsonPath("$.toCollect").value(9))
                .andExpect(jsonPath("$.credit").value(8));
    }
}
