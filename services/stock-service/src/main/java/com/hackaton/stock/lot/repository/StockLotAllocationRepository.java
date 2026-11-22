package com.hackaton.stock.lot.repository;

import com.hackaton.stock.lot.entity.StockLotAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockLotAllocationRepository extends JpaRepository<StockLotAllocation, Long> {
}
