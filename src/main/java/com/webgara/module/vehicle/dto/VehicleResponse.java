package com.webgara.module.vehicle.dto;

import com.webgara.module.vehicle.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private String id;
    private String ownerId;
    private String plateNumber;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String vin;
    private String engineNumber;
    private Integer mileage;
    private String fuelType;
    private Vehicle.MaintenanceReminder maintenanceReminder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
