package com.hackaton.stock.lot.service.reader;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.lot.dto.LotResponse;
import com.hackaton.stock.lot.mapper.StockLotMapper;
import com.hackaton.stock.lot.repository.StockLotRepository;
import com.hackaton.stock.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockLotReaderService implements StockLotReader {

    private final ProductRepository productRepository;
    private final StockLotRepository lotRepository;
    private final StockLotMapper lotMapper;

    @Override
    public List<LotResponse> findByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produit", productId);
        }
        return lotRepository.findBalancesByProductId(productId).stream()
                .map(lotMapper::toResponse)
                .toList();
    }
}
