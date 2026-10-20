
package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.dto.ApiAcceptanceResponse;
import com.freightfox.dispatch.dto.BatchOrdersRequest;
import com.freightfox.dispatch.dto.OrderRequest;
import com.freightfox.dispatch.entity.DeliveryOrder;
import com.freightfox.dispatch.service.DeliveryOrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DeliveryOrderController {

    private final DeliveryOrderService orderService;

    public DeliveryOrderController(DeliveryOrderService orderService) {
        this.orderService = orderService;
    }

    // Create a single delivery order
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryOrder createOrder(
            @Valid @RequestBody OrderRequest request) {

        return orderService.createOrder(request);
    }

    // Assignment-required batch order endpoint
    @PostMapping("/dispatch/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiAcceptanceResponse createOrders(
            @Valid @RequestBody BatchOrdersRequest request) {

        orderService.createOrders(request.orders());

        return new ApiAcceptanceResponse(
                "Delivery orders accepted.",
                "success"
        );
    }

    // Retrieve all delivery orders
    @GetMapping("/orders")
    public List<DeliveryOrder> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Retrieve a delivery order by ID
    @GetMapping("/orders/{orderId}")
    public DeliveryOrder getOrderById(
            @PathVariable String orderId) {

        return orderService.getOrderById(orderId);
    }
}
