
package com.freightfox.dispatch.service.batch;

import com.freightfox.dispatch.dto.VehicleRequest;
import com.freightfox.dispatch.entity.Vehicle;
import com.freightfox.dispatch.repository.VehicleRepository;
import com.freightfox.dispatch.service.VehicleService;

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
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleRequest firstVehicle;
    private VehicleRequest secondVehicle;

    @BeforeEach
    void setUp() {
        firstVehicle = new VehicleRequest(
                "VEH-101",
                100.0,
                17.385,
                78.4867,
                "Hyderabad Depot A"
        );

        secondVehicle = new VehicleRequest(
                "VEH-102",
                200.0,
                17.400,
                78.500,
                "Hyderabad Depot B"
        );
    }

    @Test
    void createVehiclesShouldSaveAllValidVehicles() {
        when(vehicleRepository.findAllById(List.of("VEH-101", "VEH-102")))
                .thenReturn(List.of());

        vehicleService.createVehicles(List.of(firstVehicle, secondVehicle));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Vehicle>> captor =
                (ArgumentCaptor<List<Vehicle>>) (ArgumentCaptor<?>)
                        ArgumentCaptor.forClass(List.class);

        verify(vehicleRepository).saveAll(captor.capture());

        List<Vehicle> savedVehicles = captor.getValue();

        assertEquals(2, savedVehicles.size());
        assertEquals("VEH-101", savedVehicles.get(0).getVehicleId());
        assertEquals("VEH-102", savedVehicles.get(1).getVehicleId());
        assertEquals(100.0, savedVehicles.get(0).getCapacity());
        assertEquals(200.0, savedVehicles.get(1).getCapacity());
    }

    @Test
    void createVehiclesShouldRejectNullBatch() {
        assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.createVehicles(null)
        );

        verify(vehicleRepository, never()).saveAll(anyList());
    }

    @Test
    void createVehiclesShouldRejectEmptyBatch() {
        assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.createVehicles(List.of())
        );

        verify(vehicleRepository, never()).saveAll(anyList());
    }

    @Test
    void createVehiclesShouldRejectDuplicateIdsInSameRequest() {
        assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.createVehicles(
                        List.of(firstVehicle, firstVehicle)
                )
        );

        verify(vehicleRepository, never()).saveAll(anyList());
        verify(vehicleRepository, never()).findAllById(anyList());
    }

    @Test
    void createVehiclesShouldRejectIdAlreadyInDatabase() {
        Vehicle existingVehicle = new Vehicle(
                "VEH-101",
                100.0,
                17.385,
                78.4867,
                "Existing Depot"
        );

        when(vehicleRepository.findAllById(List.of("VEH-101", "VEH-102")))
                .thenReturn(List.of(existingVehicle));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vehicleService.createVehicles(
                        List.of(firstVehicle, secondVehicle)
                )
        );

        assertTrue(exception.getMessage().contains("VEH-101"));
        verify(vehicleRepository, never()).saveAll(anyList());
    }
}
