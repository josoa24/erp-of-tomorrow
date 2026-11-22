package com.hackaton.module.record.service.values;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.entity.ReferenceTarget;
import com.hackaton.module.reference.service.ReferenceResolver;
import com.hackaton.module.reference.service.ReferenceValue;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.TestClocks;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordValuesValidatorServiceTest {

    @Mock
    private ReferenceResolver referenceResolver;

    private RecordValuesValidatorService validator;

    @BeforeEach
    void setUp() {
        validator = new RecordValuesValidatorService(new ScalarValueParser(), referenceResolver, TestClocks.SUNDAY_NOON);
    }

    @Test
    void shouldStoreResolvedReferenceAndParsedValues_whenInputIsValid() {
        when(referenceResolver.resolve(ReferenceTarget.CUSTOMER, 7L)).thenReturn(new ReferenceValue(7L, "Hery Rabe"));
        Map<String, Object> raw = new HashMap<>(Map.of("client", Map.of("id", 7), "montant", "15000", "heure", "10:30"));
        raw.put("quartier", " ");

        Map<String, Object> values = validator.validate(DefinitionFixtures.livraisons(), raw);

        assertEquals(RecordFixtures.ref(7, "Hery Rabe"), values.get("client"));
        assertEquals(new BigDecimal("15000"), values.get("montant"));
        assertEquals("10:30", values.get("heure"));
        assertFalse(values.containsKey("quartier"));
    }

    @Test
    void shouldRefuse_whenRequiredColumnIsMissing() {
        InvalidInputException error = assertThrows(InvalidInputException.class,
                () -> validator.validate(DefinitionFixtures.livraisons(), Map.of("quartier", "Isotry")));

        assertEquals("Champ obligatoire : Client", error.getMessage());
        verifyNoInteractions(referenceResolver);
    }

    @Test
    void shouldRefuse_whenAColumnIsUnknown() {
        assertThrows(InvalidInputException.class, () -> validator.validate(DefinitionFixtures.livraisons(),
                Map.of("client", Map.of("id", 7), "poids", 3)));
    }

    @Test
    void shouldRefuse_whenReferenceHasNoUsableId() {
        assertThrows(InvalidInputException.class, () -> validator.validate(DefinitionFixtures.livraisons(),
                Map.of("client", Map.of("nom", "Hery"))));
    }

    @Test
    void shouldPropagateBusinessError_whenReferenceDoesNotExist() {
        when(referenceResolver.resolve(ReferenceTarget.CUSTOMER, 99L))
                .thenThrow(new BusinessException("Référence introuvable : client n°99"));

        assertThrows(BusinessException.class, () -> validator.validate(DefinitionFixtures.livraisons(),
                Map.of("client", Map.of("id", 99))));
    }
}
