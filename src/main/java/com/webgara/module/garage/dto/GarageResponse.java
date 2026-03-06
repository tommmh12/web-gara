package com.webgara.module.garage.dto;

import com.webgara.module.garage.model.Garage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarageResponse {
    private String id;
    private String name;
    private String slug;
    private Garage.Address address;
    private String phone;
    private String email;
    private Map<String, Garage.WorkingHour> workingHours;
    private Integer capacity;
    private String description;
    private List<String> images;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
