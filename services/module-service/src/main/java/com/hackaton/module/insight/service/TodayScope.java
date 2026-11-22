package com.hackaton.module.insight.service;

import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.record.entity.ModuleRecord;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TodayScope {

    private final Clock clock;

    public boolean isToday(ModuleRecord record, ModuleColumn column) {
        LocalDate today = LocalDate.now(clock);
        Object value = record.getValues().get(column.getKey());
        return switch (column.getType()) {
            case TIME -> record.getCreatedAt().atZone(clock.getZone()).toLocalDate().equals(today);
            case DATE -> today.equals(dayOf(value, LocalDate::parse));
            case DATETIME -> today.equals(dayOf(value, text -> LocalDateTime.parse(text).toLocalDate()));
            default -> false;
        };
    }

    private LocalDate dayOf(Object value, Function<String, LocalDate> parser) {
        if (!(value instanceof String text)) {
            return null;
        }
        try {
            return parser.apply(text);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
