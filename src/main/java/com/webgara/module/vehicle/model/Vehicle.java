package com.webgara.module.vehicle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vehicles")
public class Vehicle {

    @Id
    private String id;

    @Indexed
    private String ownerId;

    @Indexed(unique = true)
    private String plateNumber;

    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String vin;
    private String engineNumber;
    private Integer mileage;
    
    // GASOLINE, DIESEL, ELECTRIC, HYBRID
    private String fuelType;

    private MaintenanceReminder maintenanceReminder;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaintenanceReminder {
        @Indexed
        private LocalDateTime nextDate;
        private Integer nextMileage;
        private Integer intervalMonths;
        private Integer intervalKm;
    }
}
