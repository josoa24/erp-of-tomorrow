package com.hackaton.vente.sale.service.counts;

import com.hackaton.vente.sale.dto.SaleCountsResponse;
import java.time.LocalDate;

public interface SaleCountsReader {

    SaleCountsResponse count(LocalDate from, LocalDate to);
}
