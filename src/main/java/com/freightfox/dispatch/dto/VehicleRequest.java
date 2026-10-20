
package com.freightfox.dispatch.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VehicleRequest(
        @NotBlank String vehicleId,

        @NotNull @Positive Double capacity,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double currentLatitude,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double currentLongitude,

        @NotBlank String currentAddress
) {
}
