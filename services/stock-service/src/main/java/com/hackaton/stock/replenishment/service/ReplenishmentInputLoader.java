package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Charge depuis la base les données d'entrée de la prévision d'un produit. */
@Component
@RequiredArgsConstructor
public class ReplenishmentInputLoader {

    public static final int ANALYSIS_DAYS = 28;
    public static final int MIN_HISTORY_DAYS = 14;

    private final StockMovementRepository movementRepository;
    private final LeadTimeResolver leadTimeResolver;

    public ReplenishmentInput load(Long productId) {
        Instant now = Instant.now();
        LocalDate firstDay = LocalDate.ofInstant(now, ZoneOffset.UTC).minusDays(ANALYSIS_DAYS - 1L);
        List<StockMovement> outs = movementRepository.findByProductIdAndTypeAndCreatedAtGreaterThanEqual(
                productId, MovementType.OUT, firstDay.atStartOfDay(ZoneOffset.UTC).toInstant());
        boolean enoughHistory = movementRepository.findFirstByProductIdOrderByCreatedAtAsc(productId)
                .map(first -> Duration.between(first.getCreatedAt(), now).toDays() >= MIN_HISTORY_DAYS)
                .orElse(false);
        return new ReplenishmentInput(
                DailyOutSeries.of(outs, firstDay, ANALYSIS_DAYS), enoughHistory, leadTimeResolver.resolve(productId));
    }
}
