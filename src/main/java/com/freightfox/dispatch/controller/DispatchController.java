
package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.dto.DispatchPlanResponse;
import com.freightfox.dispatch.dto.VehicleDispatchResponse;
// import com.freightfox.dispatch.entity.DeliveryOrder;
import com.freightfox.dispatch.service.DispatchService;
import com.freightfox.dispatch.service.optimizer.DispatchOptimizerService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    // Preserve the existing endpoint
    @PostMapping("/optimize")
    public DispatchOptimizerService.DispatchPlan optimizeDispatch() {
        return dispatchService.optimizeDispatch();
    }

    // Assignment-required response format
    @GetMapping("/plan")
    public DispatchPlanResponse getDispatchPlan() {
        DispatchOptimizerService.DispatchPlan result =
                dispatchService.optimizeDispatch();

        List<VehicleDispatchResponse> vehicles =
                result.dispatchPlan().stream()
                        .map(vehicle -> new VehicleDispatchResponse(
                                vehicle.vehicleId(),
                                vehicle.totalLoad(),
                                String.format(
                                        Locale.ROOT,
                                        "%.2f km",
                                        vehicle.totalDistance()),
                                vehicle.assignedOrders()))
                        .toList();

        return new DispatchPlanResponse(vehicles);
    }
}
