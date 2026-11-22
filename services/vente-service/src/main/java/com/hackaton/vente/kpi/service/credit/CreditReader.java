package com.hackaton.vente.kpi.service.credit;

import com.hackaton.vente.kpi.dto.CreditSummaryResponse;
import com.hackaton.vente.kpi.dto.CustomerCreditResponse;
import java.util.List;

public interface CreditReader {

    CreditSummaryResponse summarize();

    List<CustomerCreditResponse> byCustomer();
}
