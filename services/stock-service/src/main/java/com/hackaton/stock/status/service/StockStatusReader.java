package com.hackaton.stock.status.service;

import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusQuery;
import java.util.List;

public interface StockStatusReader {

    List<StockStatusItem> findAll(StockStatusQuery query);
}
