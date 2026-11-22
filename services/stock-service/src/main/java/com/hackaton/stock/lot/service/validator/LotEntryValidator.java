package com.hackaton.stock.lot.service.validator;

import com.hackaton.stock.common.exception.InvalidRequestException;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.MovementType;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class LotEntryValidator {

    private final Clock clock;

    public void validate(MovementRequest request) {
        boolean hasLotNumber = StringUtils.hasText(request.lotNumber());
        boolean hasExpiryDate = request.expiryDate() != null;
        if (!hasLotNumber && !hasExpiryDate) {
            return;
        }
        if (hasLotNumber != hasExpiryDate) {
            throw new InvalidRequestException("Le numéro de lot et la date de péremption se renseignent ensemble");
        }
        if (request.type() != MovementType.IN) {
            throw new InvalidRequestException("Un lot ne se renseigne que sur une entrée de stock");
        }
        if (request.expiryDate().isBefore(LocalDate.now(clock))) {
            throw new InvalidRequestException("La date de péremption ne peut pas être déjà passée");
        }
    }
}
