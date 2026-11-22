package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.replenishment.dto.ReplenishmentForecast;
import java.util.List;

public interface ReplenishmentReader {

    ReplenishmentForecast forProduct(Long productId);

    List<ReplenishmentForecast> findAll(boolean onlyNeeded);
}
