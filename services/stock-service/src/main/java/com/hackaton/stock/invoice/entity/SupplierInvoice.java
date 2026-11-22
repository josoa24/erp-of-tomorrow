package com.hackaton.stock.invoice.entity;

import com.hackaton.stock.invoice.shield.ShieldVerdict;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier_invoices")
@Getter
@Setter
@NoArgsConstructor
public class SupplierInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long supplierId;

    @Column(nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private String invoiceNumberKey;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    private Long purchaseOrderId;

    /** Coordonnées de paiement figurant sur la facture : jamais renvoyées en clair. */
    private String bankAccount;

    @Column(precision = 16, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 16, scale = 2)
    private BigDecimal taxAmount;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 4000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShieldVerdict verdict;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private List<SupplierInvoiceLine> lines = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "supplier_invoice_alerts", joinColumns = @JoinColumn(name = "invoice_id"))
    private List<InvoiceAlert> alerts = new ArrayList<>();

    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Long decidedBy;

    private Instant decidedAt;

    @Column(length = 1000)
    private String decisionNote;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
