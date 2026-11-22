package com.hackaton.stock.movement.repository;

import com.hackaton.stock.movement.dto.ProductOutQuantity;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Long productId);

    boolean existsByProductId(Long productId);

    List<StockMovement> findByProductIdAndTypeAndCreatedAtGreaterThanEqual(
            Long productId, MovementType type, Instant since);

    Optional<StockMovement> findFirstByProductIdOrderByCreatedAtAsc(Long productId);

    @Query("select coalesce(sum(m.quantity), 0) from StockMovement m "
            + "where m.productId = :productId and m.type = :type and m.createdAt >= :since")
    long sumQuantitySince(
            @Param("productId") Long productId,
            @Param("type") MovementType type,
            @Param("since") Instant since);

    @Query("select new com.hackaton.stock.movement.dto.ProductOutQuantity(m.productId, sum(m.quantity)) "
            + "from StockMovement m where m.type = :type and m.createdAt >= :since group by m.productId")
    List<ProductOutQuantity> sumQuantityByProductSince(
            @Param("type") MovementType type, @Param("since") Instant since);
}
