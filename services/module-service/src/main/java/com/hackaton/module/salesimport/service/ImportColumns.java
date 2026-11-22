package com.hackaton.module.salesimport.service;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.entity.ReferenceTarget;
import java.util.function.Predicate;

public record ImportColumns(String saleKey, String customerKey, String amountKey) {

    public static ImportColumns of(ModuleDefinition definition) {
        String sale = firstKey(definition, reference(ReferenceTarget.SALE));
        if (sale == null) {
            throw new BusinessException("Ce module n'a pas de colonne qui référence une vente");
        }
        return new ImportColumns(sale, firstKey(definition, reference(ReferenceTarget.CUSTOMER)),
                firstKey(definition, column -> column.getType() == ColumnType.MONEY));
    }

    private static Predicate<ModuleColumn> reference(ReferenceTarget target) {
        return column -> column.getType() == ColumnType.REFERENCE && column.getReferenceTarget() == target;
    }

    private static String firstKey(ModuleDefinition definition, Predicate<ModuleColumn> predicate) {
        return definition.getColumns().stream().filter(predicate).map(ModuleColumn::getKey).findFirst().orElse(null);
    }
}
