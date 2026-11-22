package com.hackaton.module.reference.service;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.definition.entity.ReferenceTarget;
import com.hackaton.module.reference.client.RemoteNamed;
import com.hackaton.module.reference.client.RhClient;
import com.hackaton.module.reference.client.StockClient;
import com.hackaton.module.reference.client.VenteClient;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReferenceResolverService implements ReferenceResolver {

    private static final Map<ReferenceTarget, String> NAMES = Map.of(
            ReferenceTarget.CUSTOMER, "client",
            ReferenceTarget.SALE, "vente",
            ReferenceTarget.EMPLOYEE, "employé",
            ReferenceTarget.PRODUCT, "produit",
            ReferenceTarget.SUPPLIER, "fournisseur");

    private final VenteClient venteClient;
    private final RhClient rhClient;
    private final StockClient stockClient;

    @Override
    public ReferenceValue resolve(ReferenceTarget target, Long id) {
        return lookup(target, id).map(label -> new ReferenceValue(id, label)).orElseThrow(() ->
                new BusinessException("Référence introuvable : " + NAMES.get(target) + " n°" + id));
    }

    private Optional<String> lookup(ReferenceTarget target, Long id) {
        return switch (target) {
            case CUSTOMER -> venteClient.findCustomer(id).map(RemoteNamed::name);
            case SALE -> venteClient.findSale(id).map(sale -> ReferenceLabels.sale(sale.id(), customerName(sale.customerId())));
            case EMPLOYEE -> rhClient.findEmployee(id).map(ReferenceLabels::employee);
            case PRODUCT -> stockClient.findProduct(id).map(RemoteNamed::name);
            case SUPPLIER -> stockClient.findSupplier(id).map(RemoteNamed::name);
        };
    }

    private String customerName(Long customerId) {
        return customerId == null ? null : venteClient.findCustomer(customerId).map(RemoteNamed::name).orElse(null);
    }
}
