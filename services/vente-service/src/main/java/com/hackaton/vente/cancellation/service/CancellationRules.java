package com.hackaton.vente.cancellation.service;

import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.mapper.SaleNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancellationRules {

    private final SaleCancellationRepository cancellationRepository;
    private final SalePaymentRepository paymentRepository;

    public void check(Sale sale) {
        String number = SaleNumber.of(sale.getId());
        if (sale.getStatus() != DocumentStatus.VALIDATED) {
            throw new BusinessException(
                    "La vente " + number + " est un brouillon : supprimez-la au lieu de l'annuler");
        }
        if (cancellationRepository.existsBySale_Id(sale.getId())) {
            throw new BusinessException("La vente " + number + " est déjà annulée");
        }
        if (paymentRepository.existsBySale_Id(sale.getId())) {
            throw new BusinessException(
                    "La vente " + number + " a déjà un encaissement et ne peut plus être annulée");
        }
    }
}
