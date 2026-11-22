package com.hackaton.module.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.mapper.DefinitionPartsMapper;
import com.hackaton.module.definition.mapper.ModuleDefinitionMapper;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.values.RecordValues;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.RequestFixtures;
import com.hackaton.module.support.TestClocks;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RecordPersistenceTest {

    @Autowired
    private ModuleDefinitionRepository definitionRepository;
    @Autowired
    private ModuleRecordRepository recordRepository;
    @Autowired
    private EntityManager entityManager;

    private ModuleDefinition definition;

    @BeforeEach
    void setUp() {
        definition = definitionRepository.saveAndFlush(new ModuleDefinitionMapper(new DefinitionPartsMapper())
                .toEntity(RequestFixtures.livraisons(), "livraisons", "hery.rabe@nova.mg", TestClocks.NOW));
    }

    @Test
    void shouldReloadJsonValues_withReferenceAndAmount() {
        Long id = save("a_preparer", Map.of("client", RecordFixtures.ref(7, "Hery Rabe"), "quartier", "Analakely",
                "montant", new BigDecimal("25000.50")), TestClocks.NOW).getId();
        entityManager.clear();

        ModuleRecord reloaded = recordRepository.findByIdAndDefinitionId(id, definition.getId()).orElseThrow();

        assertEquals(7L, RecordValues.referenceId(reloaded.getValues().get("client")).orElseThrow());
        assertEquals("Hery Rabe", RecordValues.label(reloaded.getValues().get("client")).orElseThrow());
        assertEquals("Analakely", reloaded.getValues().get("quartier"));
        BigDecimal amount = RecordValues.number(reloaded.getValues().get("montant")).orElseThrow();
        assertEquals(0, new BigDecimal("25000.50").compareTo(amount));
    }

    @Test
    void shouldListNewestFirst_andScopeByDefinition() {
        save("a_preparer", Map.of("quartier", "Isotry"), TestClocks.NOW.minusSeconds(60));
        save("en_route", Map.of("quartier", "Ankorondrano"), TestClocks.NOW);

        List<ModuleRecord> records = recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(definition.getId());

        assertEquals(List.of("Ankorondrano", "Isotry"),
                records.stream().map(record -> record.getValues().get("quartier")).toList());
        assertTrue(recordRepository.existsByDefinitionId(definition.getId()));
        assertTrue(recordRepository.findByIdAndDefinitionId(records.get(0).getId(), 999L).isEmpty());
    }

    @Test
    void shouldRefuseDeletingDefinition_whileRecordsReferenceIt() {
        save("a_preparer", Map.of(), TestClocks.NOW);
        entityManager.clear();

        assertThrows(PersistenceException.class, () -> {
            entityManager.createNativeQuery("delete from module.module_definition").executeUpdate();
            entityManager.flush();
        });
    }

    private ModuleRecord save(String status, Map<String, Object> values, Instant createdAt) {
        ModuleRecord record = RecordFixtures.record(definition, 0, status, values, createdAt);
        record.setId(null);
        return recordRepository.saveAndFlush(record);
    }
}
