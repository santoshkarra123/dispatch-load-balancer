
package com.freightfox.dispatch.service;

import com.freightfox.dispatch.dto.VehicleRequest;
import com.freightfox.dispatch.entity.Vehicle;
import com.freightfox.dispatch.repository.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public Vehicle createVehicle(VehicleRequest request) {
        if (vehicleRepository.existsById(request.vehicleId())) {
            throw new IllegalArgumentException(
                    "Vehicle ID already exists: " + request.vehicleId());
        }

        return vehicleRepository.save(toEntity(request));
    }

    @Transactional
    public void createVehicles(List<VehicleRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one vehicle is required.");
        }

        Set<String> ids = new HashSet<>();

        for (VehicleRequest request : requests) {
            if (!ids.add(request.vehicleId())) {
                throw new IllegalArgumentException(
                        "Duplicate vehicle ID in request: "
                                + request.vehicleId());
            }
        }

            List<String> requestedIds = requests.stream()
            .map(request -> request.vehicleId())
            .toList();

        Set<String> existingIds = new HashSet<>();

        vehicleRepository.findAllById(requestedIds)
                .forEach(vehicle ->
                        existingIds.add(vehicle.getVehicleId()));

        for (String id : requestedIds) {
            if (existingIds.contains(id)) {
                throw new IllegalArgumentException(
                        "Vehicle ID already exists: " + id);
            }
        }

        List<Vehicle> vehicles = requests.stream()
                .map(this::toEntity)
                .toList();

        vehicleRepository.saveAll(vehicles);
    }

    private Vehicle toEntity(VehicleRequest request) {
        return new Vehicle(
                request.vehicleId(),
                request.capacity(),
                request.currentLatitude(),
                request.currentLongitude(),
                request.currentAddress()
        );
    }

    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Vehicle getVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vehicle not found: " + vehicleId));
    }
}
