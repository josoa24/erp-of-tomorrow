package com.hackaton.vente.sale.service.validator;

import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.mapper.SaleResponseAssembler;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.stock.SaleStockMover;
import com.hackaton.vente.sale.service.stock.StockMove;
import com.hackaton.vente.stock.client.MovementKind;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleValidatorService implements SaleValidator {

    private final SaleRepository saleRepository;
    private final SaleStockMover stockMover;
    private final SaleResponseAssembler assembler;
    private final Clock clock;

    @Override
    public SaleResponse validate(Long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vente", id));
        if (sale.getStatus() != DocumentStatus.DRAFT) {
            throw new ImmutableResourceException("La vente " + id + " est déjà validée");
        }
        StockMove move = new StockMove(MovementKind.OUT, "Vente #" + id, "Annulation vente #" + id);
        return assembler.toResponse(stockMover.moveThen(sale.getLines(), move, () -> markValidated(sale)));
    }

    private Sale markValidated(Sale sale) {
        sale.setStatus(DocumentStatus.VALIDATED);
        sale.setValidatedAt(Instant.now(clock));
        if (sale.getPaymentMethod() == null) {
            sale.setPaymentMethod(PaymentMethod.CASH);
        }
        return saleRepository.save(sale);
    }
}
