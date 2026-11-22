package com.hackaton.vente.cancellation.entity;

import com.hackaton.vente.sale.entity.Sale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sale_cancellations",
        uniqueConstraints = @UniqueConstraint(name = "uk_sale_cancellations_sale", columnNames = "sale_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_sale_cancellations_sale"))
    private Sale sale;

    @Column(nullable = false, updatable = false, length = 500)
    private String reason;

    @Column(updatable = false)
    private Long cancelledBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public SaleCancellation(Sale sale, String reason, Long cancelledBy, Instant createdAt) {
        this.sale = sale;
        this.reason = reason;
        this.cancelledBy = cancelledBy;
        this.createdAt = createdAt;
    }
}
