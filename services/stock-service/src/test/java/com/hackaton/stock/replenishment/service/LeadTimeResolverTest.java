package com.hackaton.stock.replenishment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.replenishment.config.ReplenishmentProperties;
import com.hackaton.stock.replenishment.dto.LeadTimeSource;
import com.hackaton.stock.supplier.entity.Supplier;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeadTimeResolverTest {

    @Mock
    private PurchaseOrderRepository orderRepository;
    @Mock
    private SupplierRepository supplierRepository;

    private LeadTimeResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new LeadTimeResolver(orderRepository, supplierRepository, new ReplenishmentProperties(7, 30));
    }

    @Test
    void shouldUseSupplierDelay_whenLastOrderSupplierHasOne() {
        givenLastOrderFromSupplier(5L, 4);

        assertEquals(new LeadTime(4, LeadTimeSource.SUPPLIER, 5L), resolver.resolve(1L));
    }

    @Test
    void shouldUseDefaultButKeepSupplier_whenSupplierHasNoDelay() {
        givenLastOrderFromSupplier(5L, null);

        assertEquals(new LeadTime(7, LeadTimeSource.DEFAULT, 5L), resolver.resolve(1L));
    }

    @Test
    void shouldUseDefault_whenNoOrderContainsTheProduct() {
        when(orderRepository.findFirstByLinesProductIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.empty());

        assertEquals(new LeadTime(7, LeadTimeSource.DEFAULT, null), resolver.resolve(1L));
    }

    private void givenLastOrderFromSupplier(Long supplierId, Integer deliveryDays) {
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplierId(supplierId);
        Supplier supplier = new Supplier();
        supplier.setUsualDeliveryDays(deliveryDays);
        when(orderRepository.findFirstByLinesProductIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(order));
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
    }
}
