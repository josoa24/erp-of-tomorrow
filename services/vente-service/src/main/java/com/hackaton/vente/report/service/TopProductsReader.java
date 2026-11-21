package com.hackaton.vente.report.service;

import com.hackaton.vente.report.dto.TopProductResponse;
import java.util.List;

public interface TopProductsReader {

    List<TopProductResponse> findTop(int limit);
}
