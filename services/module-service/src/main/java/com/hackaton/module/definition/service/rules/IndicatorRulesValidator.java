package com.hackaton.module.definition.service.rules;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.dto.ColumnSpec;
import com.hackaton.module.definition.dto.IndicatorSpec;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.StatusSpec;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.IndicatorKind;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class IndicatorRulesValidator {

    private static final Set<ColumnType> TODAY_TYPES = Set.of(ColumnType.DATE, ColumnType.DATETIME, ColumnType.TIME);

    public void check(ModuleDefinitionRequest request) {
        List<IndicatorSpec> indicators = request.indicators() == null ? List.of() : request.indicators();
        DefinitionRulesValidator.requireUnique(indicators, IndicatorSpec::key, "indicateur");
        Map<String, ColumnType> columns = request.columns().stream()
                .collect(Collectors.toMap(ColumnSpec::key, ColumnSpec::type, (first, second) -> first));
        Set<String> statuses = request.statuses().stream().map(StatusSpec::key).collect(Collectors.toSet());
        indicators.forEach(indicator -> checkIndicator(indicator, columns, statuses));
    }

    private void checkIndicator(IndicatorSpec indicator, Map<String, ColumnType> columns, Set<String> statuses) {
        Function<String, InvalidInputException> invalid =
                reason -> new InvalidInputException("Indicateur " + indicator.label() + " : " + reason);
        List<String> wanted = indicator.statuses() == null ? List.of() : indicator.statuses();
        wanted.stream().filter(key -> !statuses.contains(key)).findFirst().ifPresent(key -> {
            throw invalid.apply("statut inconnu " + key);
        });
        if (indicator.kind() == IndicatorKind.SUM && columns.get(indicator.column()) != ColumnType.MONEY) {
            throw invalid.apply("la somme porte sur une colonne de montant");
        }
        if (indicator.column() != null && !columns.containsKey(indicator.column())) {
            throw invalid.apply("colonne inconnue " + indicator.column());
        }
        if (indicator.kind() == IndicatorKind.RATIO_TODAY && (indicator.todayColumn() == null || wanted.isEmpty())) {
            throw invalid.apply("un ratio du jour demande une colonne de date ou d'heure et des statuts");
        }
        if (indicator.todayColumn() != null && !TODAY_TYPES.contains(columns.get(indicator.todayColumn()))) {
            throw invalid.apply("la colonne du jour doit être une date, une date et heure ou une heure");
        }
        if (indicator.detailColumn() != null && !columns.containsKey(indicator.detailColumn())) {
            throw invalid.apply("colonne de détail inconnue " + indicator.detailColumn());
        }
    }
}
