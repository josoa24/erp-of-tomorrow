package com.hackaton.vente.sale.service.reader;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.mapper.SaleMapper;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleReaderService implements SaleReader {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;

    @Override
    public SaleResponse findById(Long id) {
        return saleRepository.findById(id)
                .map(saleMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", id));
    }

    @Override
    public List<SaleResponse> search(Long customerId, DocumentStatus status, LocalDate from, LocalDate to) {
        return saleRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(sale -> customerId == null || Objects.equals(sale.getCustomerId(), customerId))
                .filter(sale -> status == null || sale.getStatus() == status)
                .filter(sale -> from == null || !dayOf(sale).isBefore(from))
                .filter(sale -> to == null || !dayOf(sale).isAfter(to))
                .map(saleMapper::toResponse)
                .toList();
    }

    private LocalDate dayOf(Sale sale) {
        return sale.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate();
    }
}
