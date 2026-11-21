package com.hackaton.vente.sale.service.validator;

import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.sale.mapper.SaleMapper;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.stock.client.MovementKind;
import com.hackaton.vente.stock.client.StockClient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleValidatorService implements SaleValidator {

    private final SaleRepository saleRepository;
    private final StockClient stockClient;
    private final SaleMapper saleMapper;

    @Override
    public SaleResponse validate(Long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vente", id));
        if (sale.getStatus() != DocumentStatus.DRAFT) {
            throw new ImmutableResourceException("La vente " + id + " est déjà validée");
        }
        List<SaleLine> deducted = new ArrayList<>();
        try {
            for (SaleLine line : sale.getLines()) {
                stockClient.recordMovement(line.getProductId(), MovementKind.OUT, line.getQuantity(), "Vente #" + id);
                deducted.add(line);
            }
            sale.setStatus(DocumentStatus.VALIDATED);
            sale.setValidatedAt(Instant.now());
            return saleMapper.toResponse(saleRepository.save(sale));
        } catch (RuntimeException ex) {
            compensate(id, deducted);
            throw ex;
        }
    }

    private void compensate(Long saleId, List<SaleLine> deducted) {
        for (SaleLine line : deducted) {
            try {
                stockClient.recordMovement(
                        line.getProductId(), MovementKind.IN, line.getQuantity(), "Annulation vente #" + saleId);
            } catch (RuntimeException ex) {
                log.error("Annulation du stock impossible pour le produit {} (vente {})", line.getProductId(), saleId, ex);
            }
        }
    }
}
