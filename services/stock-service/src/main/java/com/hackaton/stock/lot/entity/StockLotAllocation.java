package com.hackaton.stock.lot.entity;

import com.hackaton.stock.movement.entity.StockMovement;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "stock_lot_allocations", indexes = {
        @Index(name = "ix_stock_lot_allocations_lot", columnList = "lot_id"),
        @Index(name = "ix_stock_lot_allocations_movement", columnList = "movement_id")})
@Check(name = "ck_stock_lot_allocations_quantity_positive", constraints = "quantity > 0")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockLotAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_stock_lot_allocations_lot"))
    private StockLot lot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movement_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_stock_lot_allocations_movement"))
    private StockMovement movement;

    @Column(nullable = false, updatable = false)
    private int quantity;

    public StockLotAllocation(StockLot lot, StockMovement movement, int quantity) {
        this.lot = lot;
        this.movement = movement;
        this.quantity = quantity;
    }
}
