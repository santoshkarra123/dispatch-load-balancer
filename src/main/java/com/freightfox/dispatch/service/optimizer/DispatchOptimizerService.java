
package com.freightfox.dispatch.service.optimizer;

import com.freightfox.dispatch.entity.DeliveryOrder;
import com.freightfox.dispatch.entity.Priority;
import com.freightfox.dispatch.entity.Vehicle;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DispatchOptimizerService {

    public DispatchPlan optimize(
            List<DeliveryOrder> orders,
            List<Vehicle> vehicles) {

        if (orders == null || vehicles == null) {
            throw new IllegalArgumentException(
                    "Orders and vehicles must not be null.");
        }

        List<DeliveryOrder> sortedOrders =
                new ArrayList<>(orders);

        sortedOrders.sort(
                Comparator.comparingInt(
                        order -> priorityRank(order.getPriority())));

        Map<String, VehicleAssignment> assignments =
                new LinkedHashMap<>();

        for (Vehicle vehicle : vehicles) {
            if (vehicle == null
                    || vehicle.getVehicleId() == null
                    || vehicle.getCapacity() == null
                    || vehicle.getCurrentLatitude() == null
                    || vehicle.getCurrentLongitude() == null
                    || !Double.isFinite(vehicle.getCapacity())
                    || vehicle.getCapacity() <= 0) {

                throw new IllegalArgumentException(
                        "Vehicle details are invalid.");
            }

            assignments.put(
                    vehicle.getVehicleId(),
                    new VehicleAssignment(vehicle));
        }

        List<String> unassignedOrderIds = new ArrayList<>();

        for (DeliveryOrder order : sortedOrders) {

            if (order == null
                    || order.getOrderId() == null
                    || order.getPriority() == null
                    || order.getPackageWeight() == null
                    || order.getLatitude() == null
                    || order.getLongitude() == null
                    || !Double.isFinite(order.getPackageWeight())
                    || order.getPackageWeight() <= 0) {

                throw new IllegalArgumentException(
                        "Delivery order details are invalid.");
            }

            VehicleAssignment bestAssignment = null;
            double shortestDistance = Double.MAX_VALUE;

            for (VehicleAssignment assignment : assignments.values()) {

                Vehicle vehicle = assignment.getVehicle();

                double remainingCapacity =
                        vehicle.getCapacity() - assignment.getTotalLoad();

                if (order.getPackageWeight() > remainingCapacity) {
                    continue;
                }

                // Calculate distance from this vehicle's current route position.
                double distance =
                        HaversineDistanceCalculator.calculateDistanceKm(
                                assignment.getCurrentLatitude(),
                                assignment.getCurrentLongitude(),
                                order.getLatitude(),
                                order.getLongitude());

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    bestAssignment = assignment;
                }
            }

            if (bestAssignment == null) {
                unassignedOrderIds.add(order.getOrderId());
                continue;
            }

            bestAssignment.addOrder(order, shortestDistance);
        }

        List<VehicleDispatch> dispatchPlan = new ArrayList<>();

        for (VehicleAssignment assignment : assignments.values()) {
            dispatchPlan.add(new VehicleDispatch(
                    assignment.getVehicle().getVehicleId(),
                    assignment.getTotalLoad(),
                    assignment.getTotalDistance(),
                    List.copyOf(assignment.getAssignedOrders())));
        }

        return new DispatchPlan(
                List.copyOf(dispatchPlan),
                List.copyOf(unassignedOrderIds));
    }

    private int priorityRank(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException(
                    "Order priority must not be null.");
        }

        return switch (priority) {
            case HIGH -> 0;
            case MEDIUM -> 1;
            case LOW -> 2;
        };
    }

    private static class VehicleAssignment {

        private final Vehicle vehicle;

        private final List<DeliveryOrder> assignedOrders =
                new ArrayList<>();

        private double totalLoad;
        private double totalDistance;

        // Tracks the most recently assigned delivery location.
        private double currentLatitude;
        private double currentLongitude;

        VehicleAssignment(Vehicle vehicle) {
            this.vehicle = vehicle;
            this.currentLatitude = vehicle.getCurrentLatitude();
            this.currentLongitude = vehicle.getCurrentLongitude();
        }

        void addOrder(DeliveryOrder order, double distance) {
            assignedOrders.add(order);
            totalLoad += order.getPackageWeight();
            totalDistance += distance;

            // The next delivery starts from this order's location.
            currentLatitude = order.getLatitude();
            currentLongitude = order.getLongitude();
        }

        Vehicle getVehicle() {
            return vehicle;
        }

        List<DeliveryOrder> getAssignedOrders() {
            return assignedOrders;
        }

        double getTotalLoad() {
            return totalLoad;
        }

        double getTotalDistance() {
            return totalDistance;
        }

        double getCurrentLatitude() {
            return currentLatitude;
        }

        double getCurrentLongitude() {
            return currentLongitude;
        }
    }

    public record VehicleDispatch(
            String vehicleId,
            double totalLoad,
            double totalDistance,
            List<DeliveryOrder> assignedOrders) {
    }

    public record DispatchPlan(
            List<VehicleDispatch> dispatchPlan,
            List<String> unassignedOrderIds) {
    }
}
