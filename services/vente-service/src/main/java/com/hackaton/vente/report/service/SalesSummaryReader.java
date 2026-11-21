package com.hackaton.vente.report.service;

import com.hackaton.vente.report.dto.SalesSummaryResponse;
import java.time.LocalDate;

public interface SalesSummaryReader {

    SalesSummaryResponse summarize(LocalDate from, LocalDate to);
}
