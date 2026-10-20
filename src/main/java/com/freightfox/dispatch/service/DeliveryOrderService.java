
package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.OrderRequest;
import com.freightfox.dispatch.entity.DeliveryOrder;
import com.freightfox.dispatch.repository.DeliveryOrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DeliveryOrderService {

    private final DeliveryOrderRepository orderRepository;

    public DeliveryOrderService(
            DeliveryOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public DeliveryOrder createOrder(OrderRequest request) {
        if (orderRepository.existsById(request.orderId())) {
            throw new IllegalArgumentException(
                    "Order ID already exists: " + request.orderId());
        }

        return orderRepository.save(toEntity(request));
    }

    @Transactional
    public void createOrders(List<OrderRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one order is required.");
        }

        Set<String> ids = new HashSet<>();

        for (OrderRequest request : requests) {
            if (!ids.add(request.orderId())) {
                throw new IllegalArgumentException(
                        "Duplicate order ID in request: "
                                + request.orderId());
            }
        }

            List<String> requestedIds = requests.stream()
            .map(request -> request.orderId())
            .toList();

        Set<String> existingIds = new HashSet<>();

        orderRepository.findAllById(requestedIds)
                .forEach(order -> existingIds.add(order.getOrderId()));

        for (String id : requestedIds) {
            if (existingIds.contains(id)) {
                throw new IllegalArgumentException(
                        "Order ID already exists: " + id);
            }
        }

        List<DeliveryOrder> orders = requests.stream()
                .map(this::toEntity)
                .toList();

        orderRepository.saveAll(orders);
    }

    private DeliveryOrder toEntity(OrderRequest request) {
        return new DeliveryOrder(
                request.orderId(),
                request.latitude(),
                request.longitude(),
                request.address(),
                request.packageWeight(),
                request.priority()
        );
    }

    @Transactional(readOnly = true)
    public List<DeliveryOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DeliveryOrder getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
    }
}
