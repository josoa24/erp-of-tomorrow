package com.hackaton.module.salesimport.service;

import com.hackaton.module.reference.client.RemoteNamed;
import com.hackaton.module.reference.client.RemoteSale;
import com.hackaton.module.reference.client.VenteClient;
import com.hackaton.module.reference.service.ReferenceLabels;
import com.hackaton.module.reference.service.ReferenceValue;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportedValuesBuilder {

    private final VenteClient venteClient;

    public Map<String, Object> build(RemoteSale sale, ImportColumns columns, Map<Long, Optional<String>> names) {
        Optional<String> customer = sale.customerId() == null ? Optional.empty()
                : names.computeIfAbsent(sale.customerId(), id -> venteClient.findCustomer(id).map(RemoteNamed::name));
        Map<String, Object> values = new LinkedHashMap<>();
        String saleLabel = ReferenceLabels.sale(sale.id(), customer.orElse(null));
        values.put(columns.saleKey(), new ReferenceValue(sale.id(), saleLabel).toMap());
        if (columns.customerKey() != null && customer.isPresent()) {
            values.put(columns.customerKey(), new ReferenceValue(sale.customerId(), customer.get()).toMap());
        }
        if (columns.amountKey() != null && sale.totalAmount() != null) {
            values.put(columns.amountKey(), sale.totalAmount());
        }
        return values;
    }
}
