package com.hackaton.vente.sale.service.reader;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import java.time.LocalDate;
import java.util.List;

public interface SaleReader {

    SaleResponse findById(Long id);

    List<SaleResponse> search(Long customerId, DocumentStatus status, LocalDate from, LocalDate to);
}
