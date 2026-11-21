package com.hackaton.stock.product.service.reader;

import com.hackaton.stock.product.dto.AutonomyResponse;

public interface ProductAutonomyReader {

    AutonomyResponse analyze(Long productId);
}
