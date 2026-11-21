package com.hackaton.vente.sale.service.updater;

import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.sale.mapper.SaleMapper;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.SaleLineFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleUpdaterService implements SaleUpdater {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final SaleLineFactory lineFactory;
    private final SaleMapper saleMapper;

    @Override
    public SaleResponse update(Long id, SaleRequest request) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vente", id));
        if (sale.getStatus() != DocumentStatus.DRAFT) {
            throw new ImmutableResourceException("La vente " + id + " est validée et ne peut plus être modifiée");
        }
        if (request.customerId() != null && !customerRepository.existsById(request.customerId())) {
            throw new ResourceNotFoundException("Client", request.customerId());
        }
        List<SaleLine> lines = lineFactory.build(request.lines());
        sale.setCustomerId(request.customerId());
        sale.getLines().clear();
        sale.getLines().addAll(lines);
        sale.setTotalAmount(lineFactory.total(lines));
        return saleMapper.toResponse(saleRepository.save(sale));
    }
}
