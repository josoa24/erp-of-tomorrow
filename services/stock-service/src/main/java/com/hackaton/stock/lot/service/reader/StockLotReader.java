package com.hackaton.stock.lot.service.reader;

import com.hackaton.stock.lot.dto.LotResponse;
import java.util.List;

public interface StockLotReader {

    List<LotResponse> findByProductId(Long productId);
}
