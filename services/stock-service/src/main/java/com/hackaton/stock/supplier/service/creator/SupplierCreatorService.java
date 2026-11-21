package com.hackaton.stock.supplier.service.creator;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.mapper.SupplierMapper;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierCreatorService implements SupplierCreator {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        if (supplierRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new DuplicateResourceException("Le fournisseur " + request.name().trim() + " existe déjà");
        }
        return supplierMapper.toResponse(supplierRepository.save(supplierMapper.toEntity(request)));
    }
}
