
package com.freightfox.dispatch.service;

import com.freightfox.dispatch.entity.DeliveryOrder;
import com.freightfox.dispatch.entity.Vehicle;
import com.freightfox.dispatch.repository.DeliveryOrderRepository;
import com.freightfox.dispatch.repository.VehicleRepository;
import com.freightfox.dispatch.service.optimizer.DispatchOptimizerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DispatchService {

    private final DeliveryOrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final DispatchOptimizerService optimizer;

    public DispatchService(
            DeliveryOrderRepository orderRepository,
            VehicleRepository vehicleRepository,
            DispatchOptimizerService optimizer) {
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.optimizer = optimizer;
    }

    @Transactional(readOnly = true)
    public DispatchOptimizerService.DispatchPlan optimizeDispatch() {
        List<DeliveryOrder> orders = orderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        return optimizer.optimize(orders, vehicles);
    }
}
