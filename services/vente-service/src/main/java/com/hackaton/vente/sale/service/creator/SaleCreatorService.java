package com.hackaton.vente.sale.service.creator;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
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
public class SaleCreatorService implements SaleCreator {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final SaleLineFactory lineFactory;
    private final SaleMapper saleMapper;

    @Override
    public SaleResponse create(SaleRequest request) {
        if (request.customerId() != null && !customerRepository.existsById(request.customerId())) {
            throw new ResourceNotFoundException("Client", request.customerId());
        }
        List<SaleLine> lines = lineFactory.build(request.lines());
        Sale sale = new Sale();
        sale.setCustomerId(request.customerId());
        sale.getLines().addAll(lines);
        sale.setTotalAmount(lineFactory.total(lines));
        return saleMapper.toResponse(saleRepository.save(sale));
    }
}
