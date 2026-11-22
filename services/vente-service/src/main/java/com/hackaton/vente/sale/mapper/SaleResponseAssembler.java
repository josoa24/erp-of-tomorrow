package com.hackaton.vente.sale.mapper;

import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.service.facts.SaleFacts;
import com.hackaton.vente.sale.service.facts.SaleFactsLoader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleResponseAssembler {

    private final SaleFactsLoader factsLoader;
    private final SaleMapper saleMapper;

    public SaleResponse toResponse(Sale sale) {
        return toResponses(List.of(sale)).get(0);
    }

    public List<SaleResponse> toResponses(List<Sale> sales) {
        SaleFacts facts = factsLoader.load(sales);
        return sales.stream().map(sale -> saleMapper.toResponse(sale, facts)).toList();
    }
}
