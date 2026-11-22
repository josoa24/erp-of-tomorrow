package com.hackaton.module.insight.service;

import com.hackaton.module.definition.entity.IndicatorKind;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleIndicator;
import com.hackaton.module.insight.dto.IndicatorValueResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.service.values.RecordValues;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndicatorCalculator {

    private final TodayScope todayScope;

    public IndicatorValueResponse compute(ModuleIndicator indicator, Map<String, ModuleColumn> columns,
            List<ModuleRecord> records) {
        ModuleColumn todayColumn = indicator.getTodayColumnKey() == null ? null : columns.get(indicator.getTodayColumnKey());
        List<ModuleRecord> scoped = records.stream()
                .filter(record -> todayColumn == null || todayScope.isToday(record, todayColumn))
                .toList();
        List<ModuleRecord> counted = scoped.stream()
                .filter(record -> indicator.getStatuses().isEmpty() || indicator.getStatuses().contains(record.getStatus()))
                .toList();
        boolean sum = indicator.getKind() == IndicatorKind.SUM;
        BigDecimal value = sum ? total(counted, indicator.getColumnKey()) : BigDecimal.valueOf(counted.size());
        Long total = indicator.getKind() == IndicatorKind.RATIO_TODAY ? Long.valueOf(scoped.size()) : null;
        return new IndicatorValueResponse(indicator.getKey(), indicator.getLabel(), indicator.getIcon(),
                indicator.getKind(), value, total, sum, detail(counted, indicator.getDetailColumnKey()));
    }

    private BigDecimal total(List<ModuleRecord> records, String columnKey) {
        return records.stream()
                .map(record -> RecordValues.number(record.getValues().get(columnKey)))
                .flatMap(Optional::stream)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<String> detail(List<ModuleRecord> records, String columnKey) {
        if (columnKey == null) {
            return List.of();
        }
        LinkedHashSet<String> labels = new LinkedHashSet<>();
        records.forEach(record -> RecordValues.label(record.getValues().get(columnKey)).ifPresent(labels::add));
        return List.copyOf(labels);
    }
}
