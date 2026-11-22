package com.hackaton.vente.kpi.controller;

import com.hackaton.vente.kpi.dto.CreditSummaryResponse;
import com.hackaton.vente.kpi.dto.CustomerCreditResponse;
import com.hackaton.vente.kpi.dto.SalesKpiResponse;
import com.hackaton.vente.kpi.service.credit.CreditReader;
import com.hackaton.vente.kpi.service.sales.SalesKpiReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesKpiController {

    private final SalesKpiReader kpiReader;
    private final CreditReader creditReader;

    @GetMapping("/kpis")
    public SalesKpiResponse kpis() {
        return kpiReader.read();
    }

    @GetMapping("/credit-summary")
    public CreditSummaryResponse creditSummary() {
        return creditReader.summarize();
    }

    @GetMapping("/credit-by-customer")
    public List<CustomerCreditResponse> creditByCustomer() {
        return creditReader.byCustomer();
    }
}
