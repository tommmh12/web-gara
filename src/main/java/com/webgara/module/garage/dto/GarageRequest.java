package com.webgara.module.garage.dto;

import com.webgara.module.garage.model.Garage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GarageRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Address is required")
    private Garage.Address address;

    @NotBlank(message = "Phone is required")
    private String phone;

    @Email(message = "Email is invalid")
    private String email;

    private Map<String, Garage.WorkingHour> workingHours;

    private Integer capacity;
    
    private String description;
    
    private List<String> images;

    private boolean isActive = true;
}
