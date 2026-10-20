
package com.freightfox.dispatch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchVehiclesRequest(
        @NotEmpty(message = "At least one vehicle is required.")
        List<@Valid VehicleRequest> vehicles
) {
}
