package com.hackaton.module.support;

import com.hackaton.module.definition.dto.ColumnSpec;
import com.hackaton.module.definition.dto.IndicatorSpec;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.StatusSpec;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.IndicatorKind;
import com.hackaton.module.definition.entity.ReferenceTarget;
import com.hackaton.module.definition.entity.StatusTone;
import java.util.List;

public final class RequestFixtures {

    private RequestFixtures() {
    }

    public static ModuleDefinitionRequest livraisons() {
        return request(List.of(
                new IndicatorSpec("livrees_jour", "Livrées aujourd'hui", "truck", IndicatorKind.RATIO_TODAY,
                        List.of("livree"), null, "heure", null),
                new IndicatorSpec("a_encaisser", "À encaisser", "wallet", IndicatorKind.SUM,
                        List.of("a_preparer", "en_route"), "montant", null, null)));
    }

    public static ModuleDefinitionRequest request(List<IndicatorSpec> indicators) {
        return new ModuleDefinitionRequest("Livraisons", "truck", "Suivi des livraisons du jour", List.of(
                new ColumnSpec("client", "Client", ColumnType.REFERENCE, true, null, ReferenceTarget.CUSTOMER),
                new ColumnSpec("quartier", "Quartier", ColumnType.TEXT, false, null, null),
                new ColumnSpec("mode", "Mode", ColumnType.CHOICE, false, List.of(" Moto ", "Vélo"), null),
                new ColumnSpec("heure", "Heure prévue", ColumnType.TIME, false, null, null),
                new ColumnSpec("montant", "Montant", ColumnType.MONEY, false, null, null)), List.of(
                new StatusSpec("a_preparer", "À préparer", StatusTone.WARNING, false),
                new StatusSpec("en_route", "En route", StatusTone.INFO, false),
                new StatusSpec("livree", "Livrée", StatusTone.SUCCESS, true)), indicators);
    }
}
