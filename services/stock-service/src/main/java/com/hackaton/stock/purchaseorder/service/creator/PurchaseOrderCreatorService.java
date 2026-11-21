package com.hackaton.stock.purchaseorder.service.creator;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderLineRequest;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderRequest;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import com.hackaton.stock.purchaseorder.mapper.PurchaseOrderMapper;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderCreatorService implements PurchaseOrderCreator {

    private final PurchaseOrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper orderMapper;

    @Override
    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderRequest request) {
        if (!supplierRepository.existsById(request.supplierId())) {
            throw new ResourceNotFoundException("Fournisseur", request.supplierId());
        }
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierId(request.supplierId());
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseOrderLineRequest lineRequest : request.lines()) {
            PurchaseOrderLine line = toLine(lineRequest);
            order.getLines().add(line);
            total = total.add(line.lineTotal());
        }
        order.setTotalAmount(total);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private PurchaseOrderLine toLine(PurchaseOrderLineRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.productId()));
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setProductId(product.getId());
        line.setProductName(product.getName());
        line.setQuantity(request.quantity());
        line.setUnitPrice(request.unitPrice());
        return line;
    }
}
