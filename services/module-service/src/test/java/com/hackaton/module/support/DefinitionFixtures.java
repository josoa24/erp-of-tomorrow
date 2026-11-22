package com.hackaton.module.support;

import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.IndicatorKind;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.entity.ModuleIndicator;
import com.hackaton.module.definition.entity.ModuleStatus;
import com.hackaton.module.definition.entity.ReferenceTarget;
import com.hackaton.module.definition.entity.StatusTone;
import java.util.List;

public final class DefinitionFixtures {

    private DefinitionFixtures() {
    }

    public static ModuleDefinition livraisons() {
        ModuleDefinition definition = new ModuleDefinition();
        definition.setId(10L);
        definition.setSlug("livraisons");
        definition.setName("Livraisons");
        definition.setIcon("truck");
        definition.setCreatedBy(GatewayHeaders.ADMIN_EMAIL);
        definition.setCreatedAt(TestClocks.NOW);
        add(definition, column("client", "Client", ColumnType.REFERENCE, true, ReferenceTarget.CUSTOMER));
        add(definition, column("vente", "Vente d'origine", ColumnType.REFERENCE, false, ReferenceTarget.SALE));
        add(definition, column("quartier", "Quartier", ColumnType.TEXT, false, null));
        add(definition, column("livreur", "Livreur", ColumnType.REFERENCE, false, ReferenceTarget.EMPLOYEE));
        add(definition, column("heure", "Heure prévue", ColumnType.TIME, false, null));
        add(definition, column("montant", "Montant à encaisser", ColumnType.MONEY, false, null));
        add(definition, status("a_preparer", "À préparer", StatusTone.WARNING, false));
        add(definition, status("en_route", "En route", StatusTone.INFO, false));
        add(definition, status("livree", "Livrée", StatusTone.SUCCESS, true));
        add(definition, indicator("livrees_jour", IndicatorKind.RATIO_TODAY, List.of("livree"), null, "heure", null));
        add(definition, indicator("en_route", IndicatorKind.COUNT, List.of("en_route"), null, null, "livreur"));
        add(definition, indicator("a_encaisser", IndicatorKind.SUM, List.of("a_preparer", "en_route"), "montant",
                null, null));
        return definition;
    }

    public static ModuleColumn column(String key, String label, ColumnType type, boolean required,
            ReferenceTarget target) {
        ModuleColumn column = new ModuleColumn();
        column.setKey(key);
        column.setLabel(label);
        column.setType(type);
        column.setRequired(required);
        column.setReferenceTarget(target);
        return column;
    }

    public static ModuleStatus status(String key, String label, StatusTone tone, boolean finalStatus) {
        ModuleStatus status = new ModuleStatus();
        status.setKey(key);
        status.setLabel(label);
        status.setTone(tone);
        status.setFinalStatus(finalStatus);
        return status;
    }

    public static ModuleIndicator indicator(String key, IndicatorKind kind, List<String> statuses, String column,
            String todayColumn, String detailColumn) {
        ModuleIndicator indicator = new ModuleIndicator();
        indicator.setKey(key);
        indicator.setLabel(key.replace('_', ' '));
        indicator.setIcon("truck");
        indicator.setKind(kind);
        indicator.setStatuses(statuses);
        indicator.setColumnKey(column);
        indicator.setTodayColumnKey(todayColumn);
        indicator.setDetailColumnKey(detailColumn);
        return indicator;
    }

    private static void add(ModuleDefinition definition, Object part) {
        if (part instanceof ModuleColumn column) {
            column.setPosition(definition.getColumns().size());
            column.setDefinition(definition);
            definition.getColumns().add(column);
        } else if (part instanceof ModuleStatus status) {
            status.setPosition(definition.getStatuses().size());
            status.setDefinition(definition);
            definition.getStatuses().add(status);
        } else if (part instanceof ModuleIndicator indicator) {
            indicator.setPosition(definition.getIndicators().size());
            indicator.setDefinition(definition);
            definition.getIndicators().add(indicator);
        }
    }
}
