package com.hackaton.module.record.service.values;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.reference.service.ReferenceResolver;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordValuesValidatorService implements RecordValuesValidator {

    private final ScalarValueParser scalarParser;
    private final ReferenceResolver referenceResolver;
    private final Clock clock;

    @Override
    public Map<String, Object> validate(ModuleDefinition definition, Map<String, Object> raw) {
        Set<String> keys = definition.getColumns().stream().map(ModuleColumn::getKey).collect(Collectors.toSet());
        raw.keySet().stream().filter(key -> !keys.contains(key)).findFirst().ifPresent(key -> {
            throw new InvalidInputException("Colonne inconnue : " + key);
        });
        Map<String, Object> values = new LinkedHashMap<>();
        for (ModuleColumn column : definition.getColumns()) {
            Object value = raw.get(column.getKey());
            if (RecordValues.isBlank(value)) {
                requireOptional(column);
            } else {
                values.put(column.getKey(), parse(column, value));
            }
        }
        return values;
    }

    private Object parse(ModuleColumn column, Object value) {
        if (column.getType() != ColumnType.REFERENCE) {
            return scalarParser.parse(column, value, clock.getZone());
        }
        Long id = RecordValues.referenceId(value).orElseThrow(() ->
                new InvalidInputException("Colonne " + column.getLabel() + " : référence attendue sous la forme {id}"));
        return referenceResolver.resolve(column.getReferenceTarget(), id).toMap();
    }

    private void requireOptional(ModuleColumn column) {
        if (column.isRequired()) {
            throw new InvalidInputException("Champ obligatoire : " + column.getLabel());
        }
    }
}
