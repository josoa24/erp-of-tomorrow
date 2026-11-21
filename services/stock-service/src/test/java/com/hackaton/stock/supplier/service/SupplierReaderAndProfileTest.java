package com.hackaton.stock.supplier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.supplier.dto.SupplierProfileResponse;
import com.hackaton.stock.supplier.entity.Supplier;
import com.hackaton.stock.supplier.mapper.SupplierMapper;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import com.hackaton.stock.supplier.service.profile.SupplierProfileReaderService;
import com.hackaton.stock.supplier.service.reader.SupplierReaderService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierReaderAndProfileTest {

    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private PurchaseOrderRepository orderRepository;

    private final SupplierMapper mapper = new SupplierMapper();
    private Supplier supplier;

    private Supplier supplier(Long id, String name, String phone, String email) {
        Supplier result = new Supplier();
        result.setId(id);
        result.setName(name);
        result.setPhone(phone);
        result.setEmail(email);
        return result;
    }

    private PurchaseOrder order(Long id, OrderStatus status, String total, String validatedAt, String receivedAt,
            Long productId, String productName, int quantity) {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(id);
        order.setSupplierId(1L);
        order.setStatus(status);
        order.setTotalAmount(new BigDecimal(total));
        order.setValidatedAt(validatedAt == null ? null : Instant.parse(validatedAt));
        order.setReceivedAt(receivedAt == null ? null : Instant.parse(receivedAt));
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setProductId(productId);
        line.setProductName(productName);
        line.setQuantity(quantity);
        line.setUnitPrice(BigDecimal.TEN);
        order.getLines().add(line);
        return order;
    }

    @BeforeEach
    void setUp() {
        supplier = supplier(1L, "Socolait", "0320000000", "contact@socolait.mg");
    }

    @Test
    void shouldSearchByNamePhoneAndEmail_ignoringCase() {
        when(supplierRepository.findAllByOrderByNameAsc())
                .thenReturn(List.of(supplier, supplier(2L, "Star", null, null)));
        SupplierReaderService reader = new SupplierReaderService(supplierRepository, mapper);

        assertEquals(2, reader.search(null).size());
        assertEquals(1, reader.search("SOCO").size());
        assertEquals(1, reader.search("0320").size());
        assertEquals(1, reader.search("socolait.mg").size());
        assertTrue(reader.search("zzz").isEmpty());
    }

    @Test
    void shouldComputeTotalsReceptionDelayAndSuppliedProducts() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(orderRepository.findBySupplierIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(
                order(3L, OrderStatus.DRAFT, "500", null, null, 10L, "Riz", 100),
                order(2L, OrderStatus.RECEIVED, "2000", "2026-11-10T00:00:00Z", "2026-11-14T00:00:00Z", 10L, "Riz", 50),
                order(1L, OrderStatus.RECEIVED, "1000", "2026-11-01T00:00:00Z", "2026-11-03T00:00:00Z", 20L, "Lait", 80)));

        SupplierProfileResponse profile =
                new SupplierProfileReaderService(supplierRepository, orderRepository, mapper).findProfile(1L);

        assertEquals(3, profile.ordersCount());
        assertEquals(2, profile.receivedOrdersCount());
        assertEquals(new BigDecimal("3000"), profile.totalOrdered());
        assertEquals(new BigDecimal("3.0"), profile.averageReceptionDays());
        assertEquals("Lait", profile.suppliedProducts().get(0).productName());
        assertEquals(80, profile.suppliedProducts().get(0).totalQuantity());
        assertEquals(50, profile.suppliedProducts().get(1).totalQuantity());
        assertEquals(3L, profile.recentOrders().get(0).id());
    }

    @Test
    void shouldReturnZerosAndNoDelay_whenSupplierHasNoOrder() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(orderRepository.findBySupplierIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        SupplierProfileResponse profile =
                new SupplierProfileReaderService(supplierRepository, orderRepository, mapper).findProfile(1L);

        assertEquals(0, profile.ordersCount());
        assertEquals(BigDecimal.ZERO, profile.totalOrdered());
        assertNull(profile.averageReceptionDays());
        assertTrue(profile.suppliedProducts().isEmpty());
    }

    @Test
    void shouldThrowNotFound_whenSupplierDoesNotExist() {
        when(supplierRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> new SupplierProfileReaderService(supplierRepository, orderRepository, mapper).findProfile(9L));
    }
}
