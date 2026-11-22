package com.hackaton.module.definition.service.rules;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.dto.IndicatorSpec;
import com.hackaton.module.definition.entity.IndicatorKind;
import com.hackaton.module.support.RequestFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;

class IndicatorRulesValidatorTest {

    private final IndicatorRulesValidator validator = new IndicatorRulesValidator();

    @Test
    void shouldAccept_whenIndicatorsAreAbsent() {
        assertDoesNotThrow(() -> validator.check(RequestFixtures.request(null)));
    }

    @Test
    void shouldAccept_whenCountHasDetailColumnAndTodayScope() {
        assertDoesNotThrow(() -> check(spec(IndicatorKind.COUNT, List.of("en_route"), null, "heure", "quartier")));
    }

    @Test
    void shouldRefuse_whenTwoIndicatorsShareAKey() {
        IndicatorSpec count = spec(IndicatorKind.COUNT, List.of(), null, null, null);

        assertThrows(InvalidInputException.class, () -> validator.check(RequestFixtures.request(List.of(count, count))));
    }

    @Test
    void shouldRefuse_whenIndicatorUsesUnknownStatus() {
        assertThrows(InvalidInputException.class, () -> check(spec(IndicatorKind.COUNT, List.of("perdu"), null, null, null)));
    }

    @Test
    void shouldRefuse_whenSumTargetsANonMoneyColumn() {
        assertThrows(InvalidInputException.class, () -> check(spec(IndicatorKind.SUM, List.of(), "quartier", null, null)));
    }

    @Test
    void shouldRefuse_whenSumHasNoColumn() {
        assertThrows(InvalidInputException.class, () -> check(spec(IndicatorKind.SUM, List.of(), null, null, null)));
    }

    @Test
    void shouldRefuse_whenColumnIsUnknown() {
        assertThrows(InvalidInputException.class, () -> check(spec(IndicatorKind.COUNT, List.of(), "poids", null, null)));
    }

    @Test
    void shouldRefuse_whenRatioHasNoTodayColumnOrStatuses() {
        assertThrows(InvalidInputException.class,
                () -> check(spec(IndicatorKind.RATIO_TODAY, List.of("livree"), null, null, null)));
        assertThrows(InvalidInputException.class,
                () -> check(spec(IndicatorKind.RATIO_TODAY, null, null, "heure", null)));
    }

    @Test
    void shouldRefuse_whenTodayColumnIsNotADateOrTime() {
        assertThrows(InvalidInputException.class,
                () -> check(spec(IndicatorKind.RATIO_TODAY, List.of("livree"), null, "quartier", null)));
    }

    @Test
    void shouldRefuse_whenDetailColumnIsUnknown() {
        assertThrows(InvalidInputException.class, () -> check(spec(IndicatorKind.COUNT, List.of(), null, null, "x")));
    }

    private void check(IndicatorSpec indicator) {
        validator.check(RequestFixtures.request(List.of(indicator)));
    }

    private IndicatorSpec spec(IndicatorKind kind, List<String> statuses, String column, String today, String detail) {
        return new IndicatorSpec("indicateur", "Indicateur", "truck", kind, statuses, column, today, detail);
    }
}
