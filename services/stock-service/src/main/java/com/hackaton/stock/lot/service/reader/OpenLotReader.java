package com.hackaton.stock.lot.service.reader;

import com.hackaton.stock.lot.dto.LotBalance;
import java.util.Map;

public interface OpenLotReader {

    Map<Long, LotBalance> findEarliestOpenLotByProduct();
}
