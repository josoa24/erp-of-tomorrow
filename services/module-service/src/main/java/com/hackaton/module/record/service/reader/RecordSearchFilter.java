package com.hackaton.module.record.service.reader;

import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.service.values.RecordValues;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RecordSearchFilter {

    private static final Set<ColumnType> SEARCHABLE = Set.of(ColumnType.TEXT, ColumnType.CHOICE, ColumnType.REFERENCE);

    public boolean matches(ModuleRecord record, List<ModuleColumn> columns, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String wanted = normalize(search.trim());
        return columns.stream()
                .filter(column -> SEARCHABLE.contains(column.getType()))
                .map(column -> RecordValues.label(record.getValues().get(column.getKey())))
                .flatMap(Optional::stream)
                .anyMatch(label -> normalize(label).contains(wanted));
    }

    static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
