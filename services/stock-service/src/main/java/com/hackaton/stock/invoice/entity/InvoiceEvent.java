package com.hackaton.stock.invoice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Journal d'audit d'une facture : écrit une fois, jamais modifié ni supprimé. */
@Entity
@Table(name = "supplier_invoice_events")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long invoiceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private InvoiceEventType type;

    @Column(updatable = false)
    private Long actorId;

    @Column(updatable = false)
    private String actorEmail;

    @Column(updatable = false)
    private String actorRole;

    @Column(length = 1000, updatable = false)
    private String detail;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
