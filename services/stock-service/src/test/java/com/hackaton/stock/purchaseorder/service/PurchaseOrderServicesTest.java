package com.hackaton.stock.purchaseorder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.BusinessException;
import com.hackaton.stock.common.exception.ImmutableResourceException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.service.creator.StockMovementCreator;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderLineRequest;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderRequest;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import com.hackaton.stock.purchaseorder.mapper.PurchaseOrderMapper;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.purchaseorder.service.creator.PurchaseOrderCreatorService;
import com.hackaton.stock.purchaseorder.service.receiver.PurchaseOrderReceiverService;
import com.hackaton.stock.purchaseorder.service.validator.PurchaseOrderValidatorService;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import com.hackaton.stock.supplier.service.deleter.SupplierDeleterService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServicesTest {

    @Mock
    private PurchaseOrderRepository orderRepository;
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMovementCreator movementCreator;

    private final PurchaseOrderMapper mapper = new PurchaseOrderMapper();
    private PurchaseOrder order;

    private PurchaseOrderLine line(Long productId, int quantity) {
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setProductId(productId);
        line.setProductName("Produit " + productId);
        line.setQuantity(quantity);
        line.setUnitPrice(new BigDecimal("1000"));
        return line;
    }

    @BeforeEach
    void setUp() {
        order = new PurchaseOrder();
        order.setId(1L);
        order.setSupplierId(3L);
        order.getLines().add(line(10L, 50));
        order.getLines().add(line(20L, 5));
    }

    @Test
    void shouldCreateDraftOrderWithProductNameAndTotal() {
        Product rice = new Product();
        rice.setId(10L);
        rice.setName("Riz blanc");
        when(supplierRepository.existsById(3L)).thenReturn(true);
        when(productRepository.findById(10L)).thenReturn(Optional.of(rice));
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(call -> call.getArgument(0));
        PurchaseOrderCreatorService creator =
                new PurchaseOrderCreatorService(orderRepository, supplierRepository, productRepository, mapper);

        PurchaseOrderResponse response = creator.create(new PurchaseOrderRequest(
                3L, List.of(new PurchaseOrderLineRequest(10L, 50, new BigDecimal("42000")))));

        assertEquals(OrderStatus.DRAFT, response.status());
        assertEquals("Riz blanc", response.lines().get(0).productName());
        assertEquals(new BigDecimal("2100000"), response.totalAmount());
    }

    @Test
    void shouldRefuseOrder_whenSupplierOrProductDoesNotExist() {
        PurchaseOrderCreatorService creator =
                new PurchaseOrderCreatorService(orderRepository, supplierRepository, productRepository, mapper);
        PurchaseOrderRequest request = new PurchaseOrderRequest(
                3L, List.of(new PurchaseOrderLineRequest(99L, 1, BigDecimal.ONE)));
        when(supplierRepository.existsById(3L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> creator.create(request));

        when(supplierRepository.existsById(3L)).thenReturn(true);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> creator.create(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldValidateDraftOrderOnlyOnce() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(call -> call.getArgument(0));
        PurchaseOrderValidatorService validator = new PurchaseOrderValidatorService(orderRepository, mapper);

        PurchaseOrderResponse response = validator.validate(1L);

        assertEquals(OrderStatus.VALIDATED, response.status());
        assertNotNull(response.validatedAt());
        assertThrows(ImmutableResourceException.class, () -> validator.validate(1L));
    }

    @Test
    void shouldCreateOneInMovementPerLine_whenOrderIsReceived() {
        order.setStatus(OrderStatus.VALIDATED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(PurchaseOrder.class))).thenAnswer(call -> call.getArgument(0));

        PurchaseOrderResponse response =
                new PurchaseOrderReceiverService(orderRepository, movementCreator, mapper).receive(1L);

        assertEquals(OrderStatus.RECEIVED, response.status());
        verify(movementCreator).record(eq(10L), eq(new MovementRequest(MovementType.IN, 50, "Réception commande #1")));
        verify(movementCreator).record(eq(20L), eq(new MovementRequest(MovementType.IN, 5, "Réception commande #1")));
    }

    @Test
    void shouldRefuseReception_whenOrderIsStillDraft() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class,
                () -> new PurchaseOrderReceiverService(orderRepository, movementCreator, mapper).receive(1L));
        verify(movementCreator, never()).record(any(), any());
    }

    @Test
    void shouldRefuseSecondReception_withoutNewMovements() {
        order.setStatus(OrderStatus.RECEIVED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ImmutableResourceException.class,
                () -> new PurchaseOrderReceiverService(orderRepository, movementCreator, mapper).receive(1L));
        verify(movementCreator, never()).record(any(), any());
    }

    @Test
    void shouldNotMarkReceived_whenAMovementFails() {
        order.setStatus(OrderStatus.VALIDATED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        lenient().when(movementCreator.record(eq(10L), any())).thenReturn(null);
        when(movementCreator.record(eq(20L), any())).thenThrow(new ResourceNotFoundException("Produit", 20L));

        assertThrows(ResourceNotFoundException.class,
                () -> new PurchaseOrderReceiverService(orderRepository, movementCreator, mapper).receive(1L));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldRefuseSupplierDeletion_whenItHasOrders() {
        when(supplierRepository.existsById(3L)).thenReturn(true);
        when(orderRepository.existsBySupplierId(3L)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> new SupplierDeleterService(supplierRepository, orderRepository).delete(3L));
        verify(supplierRepository, never()).deleteById(any());
    }
}
