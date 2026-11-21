package com.hackaton.stock.supplier.service.deleter;

import com.hackaton.stock.common.exception.BusinessException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierDeleterService implements SupplierDeleter {

    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository orderRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fournisseur", id);
        }
        if (orderRepository.existsBySupplierId(id)) {
            throw new BusinessException("Ce fournisseur a des bons de commande et ne peut pas être supprimé");
        }
        supplierRepository.deleteById(id);
    }
}
