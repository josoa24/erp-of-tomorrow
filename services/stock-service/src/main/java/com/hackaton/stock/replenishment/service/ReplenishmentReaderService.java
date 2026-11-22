package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.replenishment.dto.ReplenishmentForecast;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplenishmentReaderService implements ReplenishmentReader {

    private static final Comparator<ReplenishmentForecast> TO_ORDER_FIRST = Comparator
            .comparing(ReplenishmentForecast::needsReorder, Comparator.reverseOrder())
            .thenComparing(ReplenishmentForecast::coverageDays, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(ReplenishmentForecast::productName);

    private final ProductRepository productRepository;
    private final ReplenishmentInputLoader inputLoader;
    private final ReplenishmentCalculator calculator;

    @Override
    public ReplenishmentForecast forProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", productId));
        return forecast(product);
    }

    @Override
    public List<ReplenishmentForecast> findAll(boolean onlyNeeded) {
        return productRepository.findAll().stream()
                .map(this::forecast)
                .filter(forecast -> !onlyNeeded || forecast.needsReorder())
                .sorted(TO_ORDER_FIRST)
                .toList();
    }

    private ReplenishmentForecast forecast(Product product) {
        return calculator.calculate(product, inputLoader.load(product.getId()));
    }
}
