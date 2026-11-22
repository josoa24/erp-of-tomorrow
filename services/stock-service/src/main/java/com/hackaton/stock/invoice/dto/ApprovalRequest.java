package com.hackaton.stock.invoice.dto;

import jakarta.validation.constraints.Size;

/** Justification obligatoire seulement pour forcer une facture bloquée. */
public record ApprovalRequest(@Size(max = 1000) String justification) {
}
