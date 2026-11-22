package com.hackaton.stock.status.service;

import com.hackaton.stock.status.dto.StockStatusCounts;

public interface StockStatusCountsReader {

    StockStatusCounts count(String search);
}
