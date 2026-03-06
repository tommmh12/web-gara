package com.webgara.module.garage.model;

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
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "garages")
public class Garage {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String slug;

    private Address address;

    private String phone;
    private String email;

    // e.g., "monday" -> {"open": "08:00", "close": "17:30"}
    private Map<String, WorkingHour> workingHours;

    private Integer capacity;
    private String description;
    private List<String> images;

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
    public static class Address {
        private String street;
        private String ward;
        private String district;
        @Indexed
        private String city;
        private Coordinates coordinates;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        private Double lat;
        private Double lng;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingHour {
        private String open;
        private String close;
    }
}
