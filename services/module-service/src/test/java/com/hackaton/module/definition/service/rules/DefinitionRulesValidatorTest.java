package com.hackaton.module.definition.service.rules;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.dto.ColumnSpec;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.StatusSpec;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.StatusTone;
import com.hackaton.module.support.RequestFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefinitionRulesValidatorTest {

    private final DefinitionRulesValidator validator = new DefinitionRulesValidator(new IndicatorRulesValidator());

    @Test
    void shouldAccept_whenLivraisonsTemplateIsConsistent() {
        assertDoesNotThrow(() -> validator.check(RequestFixtures.livraisons()));
    }

    @Test
    void shouldRefuse_whenTwoColumnsShareAKey() {
        InvalidInputException error = assertThrows(InvalidInputException.class, () -> validator.check(withColumns(
                List.of(text("quartier"), text("quartier")))));

        assertTrue(error.getMessage().contains("quartier"));
    }

    @Test
    void shouldRefuse_whenChoiceColumnHasNoChoice() {
        ColumnSpec mode = new ColumnSpec("mode", "Mode", ColumnType.CHOICE, false, List.of(), null);

        assertThrows(InvalidInputException.class, () -> validator.check(withColumns(List.of(mode))));
    }

    @Test
    void shouldRefuse_whenChoiceColumnRepeatsAChoice() {
        ColumnSpec mode = new ColumnSpec("mode", "Mode", ColumnType.CHOICE, false, List.of("Moto", "Moto"), null);

        assertThrows(InvalidInputException.class, () -> validator.check(withColumns(List.of(mode))));
    }

    @Test
    void shouldRefuse_whenReferenceColumnHasNoTarget() {
        ColumnSpec client = new ColumnSpec("client", "Client", ColumnType.REFERENCE, true, null, null);

        assertThrows(InvalidInputException.class, () -> validator.check(withColumns(List.of(client))));
    }

    @Test
    void shouldRefuse_whenTwoStatusesShareAKey() {
        ModuleDefinitionRequest request = withStatuses(List.of(
                new StatusSpec("ouvert", "Ouvert", StatusTone.INFO, false),
                new StatusSpec("ouvert", "Fermé", StatusTone.SUCCESS, true)));

        assertThrows(InvalidInputException.class, () -> validator.check(request));
    }

    @Test
    void shouldRefuse_whenFirstStatusIsFinal() {
        ModuleDefinitionRequest request = withStatuses(List.of(
                new StatusSpec("ferme", "Fermé", StatusTone.SUCCESS, true),
                new StatusSpec("ouvert", "Ouvert", StatusTone.INFO, false)));

        assertThrows(InvalidInputException.class, () -> validator.check(request));
    }

    private ColumnSpec text(String key) {
        return new ColumnSpec(key, "Quartier", ColumnType.TEXT, false, null, null);
    }

    private ModuleDefinitionRequest withColumns(List<ColumnSpec> columns) {
        ModuleDefinitionRequest base = RequestFixtures.request(List.of());
        return new ModuleDefinitionRequest(base.name(), base.icon(), null, columns, base.statuses(), List.of());
    }

    private ModuleDefinitionRequest withStatuses(List<StatusSpec> statuses) {
        ModuleDefinitionRequest base = RequestFixtures.request(List.of());
        return new ModuleDefinitionRequest(base.name(), base.icon(), null, base.columns(), statuses, null);
    }
}
