package com.hackaton.stock.supplier.service.reader;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.mapper.SupplierMapper;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierReaderService implements SupplierReader {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponse findById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
    }

    @Override
    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll().stream().map(supplierMapper::toResponse).toList();
    }
}
