package com.hackaton.module.record.service.values;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.entity.ModuleColumn;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class ScalarValueParser {

    static final int MAX_TEXT_LENGTH = 500;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public Object parse(ModuleColumn column, Object raw, ZoneId zone) {
        return switch (column.getType()) {
            case TEXT -> text(column, raw);
            case NUMBER, MONEY -> amount(column, raw);
            case DATE -> temporal(column, "une date AAAA-MM-JJ", () -> LocalDate.parse(text(column, raw)).toString());
            case TIME -> temporal(column, "une heure HH:mm", () -> LocalTime.parse(text(column, raw), TIME).format(TIME));
            case DATETIME -> temporal(column, "une date et heure ISO", () -> dateTime(text(column, raw), zone));
            case CHOICE -> choice(column, raw);
            case REFERENCE -> throw new IllegalArgumentException("Une référence se résout auprès de son service");
        };
    }

    private String text(ModuleColumn column, Object raw) {
        if (!(raw instanceof String text)) {
            throw invalid(column, "doit être un texte");
        }
        if (text.trim().length() > MAX_TEXT_LENGTH) {
            throw invalid(column, MAX_TEXT_LENGTH + " caractères au plus");
        }
        return text.trim();
    }

    private BigDecimal amount(ModuleColumn column, Object raw) {
        BigDecimal value = RecordValues.number(raw).orElseThrow(() -> invalid(column, "doit être un nombre"));
        if (value.signum() < 0) {
            throw invalid(column, "doit être positif ou nul");
        }
        return value;
    }

    private String choice(ModuleColumn column, Object raw) {
        String value = text(column, raw);
        if (!column.getChoices().contains(value)) {
            throw invalid(column, "doit être l'un des choix " + String.join(", ", column.getChoices()));
        }
        return value;
    }

    private String dateTime(String text, ZoneId zone) {
        try {
            return OffsetDateTime.parse(text).atZoneSameInstant(zone).toLocalDateTime().format(DATE_TIME);
        } catch (DateTimeParseException ex) {
            return LocalDateTime.parse(text).format(DATE_TIME);
        }
    }

    private String temporal(ModuleColumn column, String expected, Supplier<String> parser) {
        try {
            return parser.get();
        } catch (DateTimeParseException ex) {
            throw invalid(column, "doit être " + expected);
        }
    }

    private InvalidInputException invalid(ModuleColumn column, String reason) {
        return new InvalidInputException("Colonne " + column.getLabel() + " : " + reason);
    }
}
