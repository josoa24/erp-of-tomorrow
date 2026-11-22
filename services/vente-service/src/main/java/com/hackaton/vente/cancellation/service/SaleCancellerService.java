package com.hackaton.vente.cancellation.service;

import com.hackaton.vente.cancellation.dto.CancellationRequest;
import com.hackaton.vente.cancellation.entity.SaleCancellation;
import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.mapper.SaleNumber;
import com.hackaton.vente.sale.mapper.SaleResponseAssembler;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.stock.SaleStockMover;
import com.hackaton.vente.sale.service.stock.StockMove;
import com.hackaton.vente.stock.client.MovementKind;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleCancellerService implements SaleCanceller {

    private final SaleRepository saleRepository;
    private final SaleCancellationRepository cancellationRepository;
    private final CancellationRules rules;
    private final SaleStockMover stockMover;
    private final SaleResponseAssembler assembler;
    private final Clock clock;

    @Override
    @Transactional
    public SaleResponse cancel(Long saleId, CancellationRequest request, Long cancelledBy) {
        Sale sale = saleRepository.findWithLockById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", saleId));
        rules.check(sale);
        String number = SaleNumber.of(saleId);
        SaleCancellation cancellation =
                new SaleCancellation(sale, request.reason().trim(), cancelledBy, Instant.now(clock));
        StockMove reentry = new StockMove(MovementKind.IN, "Annulation " + number, "Échec annulation " + number);
        stockMover.moveThen(sale.getLines(), reentry, () -> cancellationRepository.saveAndFlush(cancellation));
        return assembler.toResponse(sale);
    }
}
