package com.hackaton.stock.invoice.shield;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import java.util.List;

/** Un contrôle d'Invoice Shield : renvoie ses alertes, ou une liste vide si tout va bien. */
public interface InvoiceCheck {

    List<ShieldAlert> check(InvoiceCheckContext context);
}
