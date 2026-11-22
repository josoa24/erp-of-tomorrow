package com.hackaton.stock.lot.entity;

import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "stock_lots",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stock_lots_product_lot", columnNames = {"product_id", "lot_number"}),
                @UniqueConstraint(name = "uk_stock_lots_movement", columnNames = "movement_id")},
        indexes = @Index(name = "ix_stock_lots_product", columnList = "product_id"))
@Check(name = "ck_stock_lots_quantity_positive", constraints = "quantity > 0")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_stock_lots_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movement_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_stock_lots_movement"))
    private StockMovement movement;

    @Column(name = "lot_number", nullable = false, updatable = false, length = 64)
    private String lotNumber;

    @Column(nullable = false, updatable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, updatable = false)
    private int quantity;

    @Column(nullable = false, updatable = false)
    private Instant receivedAt;

    public StockLot(Product product, StockMovement movement, String lotNumber, LocalDate expiryDate,
                    Instant receivedAt) {
        this.product = product;
        this.movement = movement;
        this.lotNumber = lotNumber;
        this.expiryDate = expiryDate;
        this.quantity = movement.getQuantity();
        this.receivedAt = receivedAt;
    }
}
