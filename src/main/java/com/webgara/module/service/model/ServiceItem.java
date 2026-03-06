package com.webgara.module.service.model;

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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "services")
public class ServiceItem {

    @Id
    private String id;

    @Indexed
    private String garageId;

    private String name;
    private String description;

    @Indexed
    private String category; // OIL_CHANGE, MAINTENANCE, ENGINE, ELECTRICAL, TIRE, WASH, BODY, OTHER

    private Integer standardTime; // in minutes

    private List<Pricing> pricing;

    @Indexed
    private boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pricing {
        private String vehicleType; // SEDAN, SUV, TRUCK, VAN, MOTORCYCLE
        private Double minPrice;
        private Double maxPrice;
    }
}
