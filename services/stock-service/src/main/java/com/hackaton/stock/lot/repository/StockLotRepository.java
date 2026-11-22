package com.hackaton.stock.lot.repository;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.entity.StockLot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

    String BALANCE = "select new com.hackaton.stock.lot.dto.LotBalance(l.id, l.product.id, l.lotNumber, "
            + "l.expiryDate, l.quantity, coalesce(sum(a.quantity), 0L), l.receivedAt) "
            + "from StockLot l left join StockLotAllocation a on a.lot = l ";
    String GROUP = "group by l.id, l.product.id, l.lotNumber, l.expiryDate, l.quantity, l.receivedAt ";
    String FEFO = "order by l.expiryDate, l.receivedAt, l.id";

    boolean existsByProductIdAndLotNumber(Long productId, String lotNumber);

    @Query(BALANCE + "where l.product.id = :productId " + GROUP + FEFO)
    List<LotBalance> findBalancesByProductId(@Param("productId") Long productId);

    @Query(BALANCE + GROUP + "having l.quantity > coalesce(sum(a.quantity), 0L) " + FEFO)
    List<LotBalance> findOpenBalances();
}
