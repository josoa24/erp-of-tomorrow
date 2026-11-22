package com.hackaton.module.record.service.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.TestClocks;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ScalarValueParserTest {

    private final ScalarValueParser parser = new ScalarValueParser();

    @Test
    void shouldTrimText_whenTextIsValid() {
        assertEquals("Analakely", parse(ColumnType.TEXT, "  Analakely "));
    }

    @Test
    void shouldRefuseText_whenTooLongOrNotAString() {
        assertThrows(InvalidInputException.class, () -> parse(ColumnType.TEXT, "a".repeat(501)));
        assertThrows(InvalidInputException.class, () -> parse(ColumnType.TEXT, 12));
    }

    @Test
    void shouldParseAmount_fromNumberOrText() {
        assertEquals(new BigDecimal("25000"), parse(ColumnType.MONEY, 25000));
        assertEquals(new BigDecimal("12.5"), parse(ColumnType.NUMBER, "12,5"));
    }

    @Test
    void shouldRefuseAmount_whenNegativeOrNotANumber() {
        InvalidInputException error = assertThrows(InvalidInputException.class, () -> parse(ColumnType.MONEY, -1));

        assertTrue(error.getMessage().contains("positif"));
        assertThrows(InvalidInputException.class, () -> parse(ColumnType.NUMBER, "beaucoup"));
    }

    @Test
    void shouldKeepIsoDate_andRefuseOtherFormats() {
        assertEquals("2026-11-22", parse(ColumnType.DATE, "2026-11-22"));
        assertThrows(InvalidInputException.class, () -> parse(ColumnType.DATE, "22/11/2026"));
    }

    @Test
    void shouldKeepHourAndMinute_andRefuseSeconds() {
        assertEquals("14:30", parse(ColumnType.TIME, "14:30"));
        assertThrows(InvalidInputException.class, () -> parse(ColumnType.TIME, "14:30:00"));
        assertThrows(InvalidInputException.class, () -> parse(ColumnType.TIME, "9h30"));
    }

    @Test
    void shouldStoreLocalTime_whenDateTimeHasAnOffset() {
        assertEquals("2026-11-22T12:00", parse(ColumnType.DATETIME, "2026-11-22T09:00:00Z"));
        assertEquals("2026-11-22T08:15", parse(ColumnType.DATETIME, "2026-11-22T08:15"));
        assertThrows(InvalidInputException.class, () -> parse(ColumnType.DATETIME, "demain"));
    }

    @Test
    void shouldAcceptOnlyDeclaredChoices() {
        ModuleColumn mode = DefinitionFixtures.column("mode", "Mode", ColumnType.CHOICE, false, null);
        mode.setChoices(List.of("Moto", "Vélo"));

        assertEquals("Vélo", parser.parse(mode, " Vélo ", TestClocks.ANTANANARIVO));
        assertThrows(InvalidInputException.class, () -> parser.parse(mode, "Camion", TestClocks.ANTANANARIVO));
    }

    @Test
    void shouldRefuseReference_becauseItIsResolvedRemotely() {
        ModuleColumn client = DefinitionFixtures.column("client", "Client", ColumnType.REFERENCE, true, null);

        assertThrows(IllegalArgumentException.class, () -> parser.parse(client, 7, TestClocks.ANTANANARIVO));
    }

    private Object parse(ColumnType type, Object raw) {
        return parser.parse(DefinitionFixtures.column("colonne", "Colonne", type, false, null), raw,
                TestClocks.ANTANANARIVO);
    }
}
