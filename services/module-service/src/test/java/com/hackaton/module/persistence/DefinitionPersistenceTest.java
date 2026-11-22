package com.hackaton.module.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.mapper.DefinitionPartsMapper;
import com.hackaton.module.definition.mapper.ModuleDefinitionMapper;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.support.RequestFixtures;
import com.hackaton.module.support.TestClocks;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class DefinitionPersistenceTest {

    private final ModuleDefinitionMapper mapper = new ModuleDefinitionMapper(new DefinitionPartsMapper());

    @Autowired
    private ModuleDefinitionRepository definitionRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReloadPartsInOrderWithChoices_whenDefinitionIsSaved() {
        Long id = definitionRepository.saveAndFlush(livraisons("livraisons")).getId();
        entityManager.clear();

        ModuleDefinition reloaded = definitionRepository.findBySlug("livraisons").orElseThrow();

        assertEquals(id, reloaded.getId());
        assertEquals(List.of("client", "quartier", "mode", "heure", "montant"),
                reloaded.getColumns().stream().map(ModuleColumn::getKey).toList());
        assertEquals(List.of("Moto", "Vélo"), reloaded.getColumns().get(2).getChoices());
        assertEquals(List.of("a_preparer", "en_route"), reloaded.getIndicators().get(1).getStatuses());
        assertTrue(reloaded.getStatuses().get(2).isFinalStatus());
    }

    @Test
    void shouldRefuseSecondDefinition_whenSlugIsTaken() {
        definitionRepository.saveAndFlush(livraisons("livraisons"));

        assertThrows(DataIntegrityViolationException.class,
                () -> definitionRepository.saveAndFlush(livraisons("livraisons")));
    }

    @Test
    void shouldRefuseDuplicateColumnKey_withinOneDefinition() {
        ModuleDefinition definition = livraisons("doublon");
        definition.getColumns().get(1).setKey("client");

        assertThrows(DataIntegrityViolationException.class, () -> definitionRepository.saveAndFlush(definition));
    }

    @Test
    void shouldDeleteParts_whenDefinitionIsDeleted() {
        ModuleDefinition saved = definitionRepository.saveAndFlush(livraisons("livraisons"));

        definitionRepository.delete(saved);
        definitionRepository.flush();

        Long columns = entityManager.createQuery("select count(c) from ModuleColumn c", Long.class).getSingleResult();
        assertEquals(0L, columns);
        assertFalse(definitionRepository.existsBySlug("livraisons"));
    }

    private ModuleDefinition livraisons(String slug) {
        return mapper.toEntity(RequestFixtures.livraisons(), slug, "hery.rabe@nova.mg", TestClocks.NOW);
    }
}
