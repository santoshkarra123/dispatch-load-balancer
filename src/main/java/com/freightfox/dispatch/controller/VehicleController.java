
package com.freightfox.dispatch.controller;

import com.freightfox.dispatch.dto.ApiAcceptanceResponse;
import com.freightfox.dispatch.dto.BatchVehiclesRequest;
import com.freightfox.dispatch.dto.VehicleRequest;
import com.freightfox.dispatch.entity.Vehicle;
import com.freightfox.dispatch.service.VehicleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // Create a single vehicle
    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle createVehicle(
            @Valid @RequestBody VehicleRequest request) {

        return vehicleService.createVehicle(request);
    }

    // Assignment-required batch vehicle endpoint
    @PostMapping("/dispatch/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiAcceptanceResponse createVehicles(
            @Valid @RequestBody BatchVehiclesRequest request) {

        vehicleService.createVehicles(request.vehicles());

        return new ApiAcceptanceResponse(
                "Vehicle details accepted.",
                "success"
        );
    }

    // Retrieve all vehicles
    @GetMapping("/vehicles")
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    // Retrieve a vehicle by ID
    @GetMapping("/vehicles/{vehicleId}")
    public Vehicle getVehicleById(
            @PathVariable String vehicleId) {

        return vehicleService.getVehicleById(vehicleId);
    }
}
