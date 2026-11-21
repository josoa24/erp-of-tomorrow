package com.hackaton.stock.status.service;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.status.dto.StockStatusItem;
import java.util.List;

public interface StockStatusReader {

    List<StockStatusItem> findAll(StockRisk risk);
}
