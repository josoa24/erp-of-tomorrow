package com.hackaton.stock.lot.mapper;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.dto.LotResponse;
import org.springframework.stereotype.Component;

@Component
public class StockLotMapper {

    public LotResponse toResponse(LotBalance balance) {
        return new LotResponse(
                balance.lotNumber(),
                balance.expiryDate(),
                balance.quantity(),
                balance.remaining(),
                balance.receivedAt());
    }
}
