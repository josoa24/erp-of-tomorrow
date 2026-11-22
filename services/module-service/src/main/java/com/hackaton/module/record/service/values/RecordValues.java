package com.hackaton.module.record.service.values;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public final class RecordValues {

    static final String ID = "id";
    static final String LABEL = "label";

    private RecordValues() {
    }

    public static boolean isBlank(Object raw) {
        return raw == null
                || raw instanceof String text && text.isBlank()
                || raw instanceof Map<?, ?> map && map.isEmpty();
    }

    public static Optional<String> label(Object stored) {
        if (isBlank(stored)) {
            return Optional.empty();
        }
        Object label = stored instanceof Map<?, ?> map ? map.get(LABEL) : stored;
        return Optional.ofNullable(label).map(String::valueOf);
    }

    public static Optional<Long> referenceId(Object raw) {
        Object id = raw instanceof Map<?, ?> map ? map.get(ID) : raw;
        return number(id).filter(value -> value.signum() > 0 && value.stripTrailingZeros().scale() <= 0)
                .map(BigDecimal::longValueExact);
    }

    public static Optional<BigDecimal> number(Object raw) {
        if (raw instanceof Number number) {
            return Optional.of(new BigDecimal(number.toString()));
        }
        if (raw instanceof String text && !text.isBlank()) {
            try {
                return Optional.of(new BigDecimal(text.trim().replace(',', '.')));
            } catch (NumberFormatException ex) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
