package com.hackaton.vente.sale.service.deleter;

import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleDeleterService implements SaleDeleter {

    private final SaleRepository saleRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vente", id));
        if (sale.getStatus() != DocumentStatus.DRAFT) {
            throw new ImmutableResourceException("La vente " + id + " est validée et ne peut pas être supprimée");
        }
        saleRepository.delete(sale);
    }
}
