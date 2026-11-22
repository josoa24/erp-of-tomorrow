package com.hackaton.stock.lot.service.reader;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.repository.StockLotRepository;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpenLotReaderService implements OpenLotReader {

    private final StockLotRepository lotRepository;

    @Override
    public Map<Long, LotBalance> findEarliestOpenLotByProduct() {
        return lotRepository.findOpenBalances().stream()
                .collect(Collectors.toMap(LotBalance::productId, Function.identity(), (earliest, later) -> earliest));
    }
}
