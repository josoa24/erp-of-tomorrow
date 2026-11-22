package com.hackaton.vente.sale.service.reader;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.mapper.SaleResponseAssembler;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleReaderService implements SaleReader {

    private final SaleRepository saleRepository;
    private final SaleResponseAssembler assembler;
    private final SaleSearchFilter filter;

    @Override
    public SaleResponse findById(Long id) {
        return saleRepository.findById(id)
                .map(assembler::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", id));
    }

    @Override
    public PageResult<SaleResponse> search(SaleSearchCriteria criteria, Integer page, Integer size) {
        List<Sale> stored = saleRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(sale -> filter.matchesStored(sale, criteria))
                .toList();
        List<SaleResponse> matches = assembler.toResponses(stored).stream()
                .filter(sale -> filter.matchesDerived(sale, criteria))
                .toList();
        return PageResult.of(matches, page, size);
    }
}
