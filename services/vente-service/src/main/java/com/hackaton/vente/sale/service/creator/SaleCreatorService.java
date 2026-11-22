package com.hackaton.vente.sale.service.creator;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.sale.mapper.SaleResponseAssembler;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.SaleLineFactory;
import com.hackaton.vente.sale.service.terms.SaleTermsApplier;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleCreatorService implements SaleCreator {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final SaleLineFactory lineFactory;
    private final SaleTermsApplier termsApplier;
    private final SaleResponseAssembler assembler;

    @Override
    @Transactional
    public SaleResponse create(SaleRequest request) {
        if (request.customerId() != null && !customerRepository.existsById(request.customerId())) {
            throw new ResourceNotFoundException("Client", request.customerId());
        }
        Sale sale = new Sale();
        sale.setCustomerId(request.customerId());
        termsApplier.apply(sale, request);
        List<SaleLine> lines = lineFactory.build(request.lines());
        sale.getLines().addAll(lines);
        sale.setTotalAmount(lineFactory.total(lines));
        return assembler.toResponse(saleRepository.save(sale));
    }
}
