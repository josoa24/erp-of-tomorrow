package com.hackaton.stock.lot.service.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hackaton.stock.common.exception.InvalidRequestException;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.MovementType;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class LotEntryValidatorTest {

    private static final ZoneId ANTANANARIVO = ZoneId.of("Indian/Antananarivo");
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 22);

    private final LotEntryValidator validator = new LotEntryValidator(
            Clock.fixed(Instant.parse("2026-11-21T22:30:00Z"), ANTANANARIVO));

    private MovementRequest request(MovementType type, String lotNumber, LocalDate expiryDate) {
        return new MovementRequest(type, 10, "Livraison", lotNumber, expiryDate);
    }

    @Test
    void shouldAccept_whenNoLotFieldIsGiven() {
        assertDoesNotThrow(() -> validator.validate(request(MovementType.OUT, null, null)));
    }

    @Test
    void shouldAccept_whenEntryHasLotAndFutureExpiry() {
        assertDoesNotThrow(() -> validator.validate(request(MovementType.IN, "L-2611", TODAY.plusDays(23))));
    }

    @Test
    void shouldAccept_whenExpiryIsTodayInAntananarivo() {
        assertDoesNotThrow(() -> validator.validate(request(MovementType.IN, "L-2611", TODAY)));
    }

    @Test
    void shouldReject_whenOnlyLotNumberIsGiven() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validate(request(MovementType.IN, "L-2611", null)));
    }

    @Test
    void shouldReject_whenOnlyExpiryDateIsGiven() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validate(request(MovementType.IN, null, TODAY.plusDays(5))));
    }

    @Test
    void shouldReject_whenLotNumberIsBlankWithExpiryDate() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validate(request(MovementType.IN, "   ", TODAY.plusDays(5))));
    }

    @Test
    void shouldReject_whenLotIsGivenOnStockExit() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validate(request(MovementType.OUT, "L-2611", TODAY.plusDays(5))));
    }

    @Test
    void shouldReject_whenExpiryWasYesterdayInAntananarivo() {
        assertThrows(InvalidRequestException.class,
                () -> validator.validate(request(MovementType.IN, "L-2611", TODAY.minusDays(1))));
    }
}
