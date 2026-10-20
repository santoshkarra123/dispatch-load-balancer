
package com.freightfox.dispatch.service.optimizer;

import com.freightfox.dispatch.entity.DeliveryOrder;
import com.freightfox.dispatch.entity.Priority;
import com.freightfox.dispatch.entity.Vehicle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DispatchOptimizerServiceTest {

    private DispatchOptimizerService optimizer;

    @BeforeEach
    void setUp() {
        optimizer = new DispatchOptimizerService();
    }

    @Test
    void shouldAssignOrderToNearestVehicle() {
        Vehicle nearVehicle = new Vehicle(
                "V1", 100.0, 17.3850, 78.4867, "Hyderabad");

        Vehicle farVehicle = new Vehicle(
                "V2", 100.0, 16.5062, 80.6480, "Vijayawada");

        DeliveryOrder order = new DeliveryOrder(
                "O1", 17.3900, 78.4900,
                "Delivery A", 10.0, Priority.MEDIUM);

        DispatchOptimizerService.DispatchPlan result =
                optimizer.optimize(
                        List.of(order),
                        List.of(nearVehicle, farVehicle));

        // Both vehicles should appear in the dispatch plan.
        assertEquals(2, result.dispatchPlan().size());

        // The nearest vehicle should receive the order.
        assertEquals(
                "V1",
                result.dispatchPlan().get(0).vehicleId());

        assertEquals(
                10.0,
                result.dispatchPlan().get(0).totalLoad(),
                0.000001);

        assertEquals(
                1,
                result.dispatchPlan().get(0).assignedOrders().size());

        // The second vehicle should remain unassigned.
        assertEquals(
                "V2",
                result.dispatchPlan().get(1).vehicleId());

        assertEquals(
                0,
                result.dispatchPlan().get(1).assignedOrders().size());

        assertEquals(0, result.unassignedOrderIds().size());
    }

    @Test
    void shouldNotExceedVehicleCapacity() {
        Vehicle vehicle = new Vehicle(
                "V1", 10.0, 17.3850, 78.4867, "Hyderabad");

        DeliveryOrder firstOrder = new DeliveryOrder(
                "O1", 17.3900, 78.4900,
                "Delivery A", 7.0, Priority.HIGH);

        DeliveryOrder secondOrder = new DeliveryOrder(
                "O2", 17.4000, 78.5000,
                "Delivery B", 5.0, Priority.MEDIUM);

        DispatchOptimizerService.DispatchPlan result =
                optimizer.optimize(
                        List.of(firstOrder, secondOrder),
                        List.of(vehicle));

        assertEquals(
                7.0,
                result.dispatchPlan().get(0).totalLoad(),
                0.000001);

        assertEquals(
                1,
                result.dispatchPlan().get(0).assignedOrders().size());

        assertEquals(
                List.of("O2"),
                result.unassignedOrderIds());
    }

    @Test
    void shouldProcessHighPriorityOrdersFirst() {
        Vehicle vehicle = new Vehicle(
                "V1", 10.0, 17.3850, 78.4867, "Hyderabad");

        DeliveryOrder lowPriority = new DeliveryOrder(
                "O1", 17.3900, 78.4900,
                "Delivery A", 8.0, Priority.LOW);

        DeliveryOrder highPriority = new DeliveryOrder(
                "O2", 17.4000, 78.5000,
                "Delivery B", 8.0, Priority.HIGH);

        DispatchOptimizerService.DispatchPlan result =
                optimizer.optimize(
                        List.of(lowPriority, highPriority),
                        List.of(vehicle));

        assertEquals(
                "O2",
                result.dispatchPlan().get(0)
                        .assignedOrders().get(0).getOrderId());

        assertEquals(
                List.of("O1"),
                result.unassignedOrderIds());
    }

    @Test
    void shouldReturnAllOrdersAsUnassignedWhenNoVehiclesExist() {
        DeliveryOrder order = new DeliveryOrder(
                "O1", 17.3900, 78.4900,
                "Delivery A", 10.0, Priority.HIGH);

        DispatchOptimizerService.DispatchPlan result =
                optimizer.optimize(
                        List.of(order),
                        List.of());

        assertEquals(0, result.dispatchPlan().size());

        assertEquals(
                List.of("O1"),
                result.unassignedOrderIds());
    }

    @Test
    void shouldRejectNullOrderList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> optimizer.optimize(null, List.of()));
    }

    @Test
    void shouldCalculateDistanceSequentiallyBetweenAssignedOrders() {
        Vehicle vehicle = new Vehicle(
                "V1", 100.0, 17.3850, 78.4867, "Hyderabad");

        DeliveryOrder firstOrder = new DeliveryOrder(
                "O1", 17.3900, 78.4900,
                "Delivery A", 10.0, Priority.HIGH);

        DeliveryOrder secondOrder = new DeliveryOrder(
                "O2", 17.4000, 78.5000,
                "Delivery B", 10.0, Priority.MEDIUM);

        DispatchOptimizerService.DispatchPlan result =
                optimizer.optimize(
                        List.of(firstOrder, secondOrder),
                        List.of(vehicle));

        double firstLeg =
                HaversineDistanceCalculator.calculateDistanceKm(
                        vehicle.getCurrentLatitude(),
                        vehicle.getCurrentLongitude(),
                        firstOrder.getLatitude(),
                        firstOrder.getLongitude());

        double secondLeg =
                HaversineDistanceCalculator.calculateDistanceKm(
                        firstOrder.getLatitude(),
                        firstOrder.getLongitude(),
                        secondOrder.getLatitude(),
                        secondOrder.getLongitude());

        double expectedDistance = firstLeg + secondLeg;

        assertEquals(
                expectedDistance,
                result.dispatchPlan().get(0).totalDistance(),
                0.000001);

        assertEquals(
                2,
                result.dispatchPlan().get(0).assignedOrders().size());
    }
}
