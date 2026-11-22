package com.hackaton.stock.invoice.entity;

import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.AlertSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Alerte levée par Invoice Shield, conservée avec la facture. */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceAlert {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertCode code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(nullable = false, length = 500)
    private String message;
}
