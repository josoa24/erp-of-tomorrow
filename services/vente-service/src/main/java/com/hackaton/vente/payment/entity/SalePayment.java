package com.hackaton.vente.payment.entity;

import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "sale_payments", indexes = @Index(name = "idx_sale_payments_sale", columnList = "sale_id"))
@Check(name = "ck_sale_payments_amount_positive", constraints = "amount > 0")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SalePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_sale_payments_sale"))
    private Sale sale;

    @Column(nullable = false, updatable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 20)
    private PaymentMethod method;

    @Column(nullable = false, updatable = false)
    private Instant paidAt;

    @Column(length = 64, updatable = false)
    private String reference;

    @Column(updatable = false)
    private Long recordedBy;

    public SalePayment(Sale sale, BigDecimal amount, PaymentMethod method, Instant paidAt, String reference,
            Long recordedBy) {
        this.sale = sale;
        this.amount = amount;
        this.method = method;
        this.paidAt = paidAt;
        this.reference = reference;
        this.recordedBy = recordedBy;
    }
}
