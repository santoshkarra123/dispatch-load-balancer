
package com.freightfox.dispatch.service.batch;

import com.freightfox.dispatch.dto.OrderRequest;
import com.freightfox.dispatch.entity.DeliveryOrder;
import com.freightfox.dispatch.entity.Priority;
import com.freightfox.dispatch.repository.DeliveryOrderRepository;
import com.freightfox.dispatch.service.DeliveryOrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryOrderServiceTest {

    @Mock
    private DeliveryOrderRepository orderRepository;

    @InjectMocks
    private DeliveryOrderService orderService;

    private OrderRequest firstOrder;
    private OrderRequest secondOrder;

    @BeforeEach
    void setUp() {
        firstOrder = new OrderRequest(
                "ORD-101",
                17.385,
                78.4867,
                "Hyderabad Address A",
                10.0,
                Priority.HIGH
        );

        secondOrder = new OrderRequest(
                "ORD-102",
                17.390,
                78.490,
                "Hyderabad Address B",
                15.0,
                Priority.MEDIUM
        );
    }

    @Test
    void createOrdersShouldSaveAllValidOrders() {
        when(orderRepository.findAllById(List.of("ORD-101", "ORD-102")))
                .thenReturn(List.of());

        orderService.createOrders(List.of(firstOrder, secondOrder));

            @SuppressWarnings("unchecked")
    ArgumentCaptor<List<DeliveryOrder>> captor =
            (ArgumentCaptor<List<DeliveryOrder>>) (ArgumentCaptor<?>)
                    ArgumentCaptor.forClass(List.class);

        verify(orderRepository).saveAll(captor.capture());

        List<DeliveryOrder> savedOrders = captor.getValue();

        assertEquals(2, savedOrders.size());
        assertEquals("ORD-101", savedOrders.get(0).getOrderId());
        assertEquals("ORD-102", savedOrders.get(1).getOrderId());
        assertEquals(10.0, savedOrders.get(0).getPackageWeight());
        assertEquals(Priority.HIGH, savedOrders.get(0).getPriority());
    }

    @Test
    void createOrdersShouldRejectNullBatch() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrders(null)
        );

        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void createOrdersShouldRejectEmptyBatch() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrders(List.of())
        );

        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    void createOrdersShouldRejectDuplicateIdsInSameRequest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrders(
                        List.of(firstOrder, firstOrder)
                )
        );

        verify(orderRepository, never()).saveAll(anyList());
        verify(orderRepository, never()).findAllById(anyList());
    }

    @Test
    void createOrdersShouldRejectIdAlreadyInDatabase() {
        DeliveryOrder existingOrder = new DeliveryOrder(
                "ORD-101",
                17.385,
                78.4867,
                "Existing Address",
                10.0,
                Priority.HIGH
        );

        when(orderRepository.findAllById(List.of("ORD-101", "ORD-102")))
                .thenReturn(List.of(existingOrder));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrders(
                        List.of(firstOrder, secondOrder)
                )
        );

        assertTrue(exception.getMessage().contains("ORD-101"));
        verify(orderRepository, never()).saveAll(anyList());
    }
}
