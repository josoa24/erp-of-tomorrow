package com.hackaton.module.definition.mapper;

import com.hackaton.module.definition.dto.ColumnSpec;
import com.hackaton.module.definition.dto.IndicatorSpec;
import com.hackaton.module.definition.dto.StatusSpec;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleIndicator;
import com.hackaton.module.definition.entity.ModuleStatus;
import com.hackaton.module.definition.entity.ReferenceTarget;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DefinitionPartsMapper {

    public ColumnSpec toSpec(ModuleColumn column) {
        return new ColumnSpec(column.getKey(), column.getLabel(), column.getType(), column.isRequired(),
                column.getType() == ColumnType.CHOICE ? column.getChoices() : null, column.getReferenceTarget(),
                column.isListed());
    }

    public StatusSpec toSpec(ModuleStatus status) {
        return new StatusSpec(status.getKey(), status.getLabel(), status.getTone(), status.isFinalStatus());
    }

    public IndicatorSpec toSpec(ModuleIndicator indicator) {
        return new IndicatorSpec(indicator.getKey(), indicator.getLabel(), indicator.getIcon(), indicator.getKind(),
                indicator.getStatuses(), indicator.getColumnKey(), indicator.getTodayColumnKey(),
                indicator.getDetailColumnKey());
    }

    public ModuleColumn toEntity(ColumnSpec spec, int position) {
        ModuleColumn column = new ModuleColumn();
        column.setPosition(position);
        column.setKey(spec.key());
        column.setLabel(spec.label().trim());
        column.setType(spec.type());
        column.setRequired(spec.required());
        column.setListed(spec.shownInList());
        column.setChoices(spec.type() == ColumnType.CHOICE ? trimmed(spec.choices()) : List.of());
        ReferenceTarget target = spec.type() == ColumnType.REFERENCE ? spec.reference() : null;
        column.setReferenceTarget(target);
        return column;
    }

    public ModuleStatus toEntity(StatusSpec spec, int position) {
        ModuleStatus status = new ModuleStatus();
        status.setPosition(position);
        status.setKey(spec.key());
        status.setLabel(spec.label().trim());
        status.setTone(spec.tone());
        status.setFinalStatus(spec.finalStatus());
        return status;
    }

    public ModuleIndicator toEntity(IndicatorSpec spec, int position) {
        ModuleIndicator indicator = new ModuleIndicator();
        indicator.setPosition(position);
        indicator.setKey(spec.key());
        indicator.setLabel(spec.label().trim());
        indicator.setIcon(spec.icon());
        indicator.setKind(spec.kind());
        indicator.setStatuses(spec.statuses() == null ? List.of() : List.copyOf(spec.statuses()));
        indicator.setColumnKey(spec.column());
        indicator.setTodayColumnKey(spec.todayColumn());
        indicator.setDetailColumnKey(spec.detailColumn());
        return indicator;
    }

    private List<String> trimmed(List<String> values) {
        return values == null ? List.of() : values.stream().map(String::trim).toList();
    }
}
