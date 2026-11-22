package com.hackaton.vente.catalog.service;

import com.hackaton.vente.catalog.dto.CatalogProductResponse;
import java.util.List;

public interface CatalogReader {

    List<CatalogProductResponse> search(String search);
}
