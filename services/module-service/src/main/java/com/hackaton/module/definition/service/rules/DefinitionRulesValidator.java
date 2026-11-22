package com.hackaton.module.definition.service.rules;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.dto.ColumnSpec;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.StatusSpec;
import com.hackaton.module.definition.entity.ColumnType;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefinitionRulesValidator implements DefinitionRules {

    private final IndicatorRulesValidator indicatorRules;

    @Override
    public void check(ModuleDefinitionRequest request) {
        requireUnique(request.columns(), ColumnSpec::key, "colonne");
        requireUnique(request.statuses(), StatusSpec::key, "statut");
        request.columns().forEach(this::checkColumn);
        if (request.statuses().get(0).finalStatus()) {
            throw new InvalidInputException("Le premier statut ne peut pas être final");
        }
        indicatorRules.check(request);
    }

    static <T> void requireUnique(List<T> items, Function<T, String> key, String kind) {
        HashSet<String> seen = new HashSet<>();
        items.stream().map(key).filter(value -> !seen.add(value)).findFirst().ifPresent(duplicate -> {
            throw new InvalidInputException("Clé de " + kind + " en double : " + duplicate);
        });
    }

    private void checkColumn(ColumnSpec column) {
        if (column.type() == ColumnType.CHOICE && (column.choices() == null || column.choices().isEmpty())) {
            throw new InvalidInputException("La colonne " + column.label() + " doit proposer au moins un choix");
        }
        if (column.type() == ColumnType.CHOICE
                && column.choices().stream().map(String::trim).distinct().count() != column.choices().size()) {
            throw new InvalidInputException("La colonne " + column.label() + " propose deux fois le même choix");
        }
        if (column.type() == ColumnType.REFERENCE && column.reference() == null) {
            throw new InvalidInputException("La colonne " + column.label() + " doit indiquer ce qu'elle référence");
        }
    }
}
