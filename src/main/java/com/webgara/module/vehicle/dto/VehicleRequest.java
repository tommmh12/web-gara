package com.webgara.module.vehicle.dto;

import com.webgara.module.vehicle.model.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    // Note: ownerId might be set from the authenticated JWT token instead of the request body for security.
    // However, we include it here for admin creation or explicit matching.
    private String ownerId;

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    private Integer year;

    private String color;
    private String vin;
    private String engineNumber;

    @PositiveOrZero(message = "Mileage must be positive or zero")
    private Integer mileage;

    @NotBlank(message = "Fuel type is required")
    private String fuelType;

    private Vehicle.MaintenanceReminder maintenanceReminder;
}
