package com.hackaton.stock.supplier.service.updater;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.entity.Supplier;
import com.hackaton.stock.supplier.mapper.SupplierMapper;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierUpdaterService implements SupplierUpdater {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
        String newName = request.name().trim();
        boolean renamed = !supplier.getName().equalsIgnoreCase(newName);
        if (renamed && supplierRepository.existsByNameIgnoreCase(newName)) {
            throw new DuplicateResourceException("Le fournisseur " + newName + " existe déjà");
        }
        supplierMapper.apply(supplier, request);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }
}
