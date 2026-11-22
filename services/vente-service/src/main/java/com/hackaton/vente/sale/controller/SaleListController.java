package com.hackaton.vente.sale.controller;

import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleCountsResponse;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.service.counts.SaleCountsReader;
import com.hackaton.vente.sale.service.reader.SaleReader;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleListController {

    public static final String TOTAL_COUNT_HEADER = "X-Total-Count";

    private final SaleReader saleReader;
    private final SaleCountsReader countsReader;

    @GetMapping
    public ResponseEntity<List<SaleResponse>> search(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<PaymentStatus> paymentStatus,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) Boolean toDeliver,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        SaleSearchCriteria criteria = new SaleSearchCriteria(
                customerId, status, from, to, search, paymentStatus, paymentMethod, toDeliver);
        PageResult<SaleResponse> result = saleReader.search(criteria, page, size);
        return ResponseEntity.ok()
                .header(TOTAL_COUNT_HEADER, String.valueOf(result.total()))
                .body(result.items());
    }

    @GetMapping("/counts")
    public SaleCountsResponse counts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return countsReader.count(from, to);
    }
}
