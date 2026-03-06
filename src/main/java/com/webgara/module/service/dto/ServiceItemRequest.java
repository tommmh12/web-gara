package com.webgara.module.service.dto;

import com.webgara.module.service.model.ServiceItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceItemRequest {

    @NotBlank(message = "Garage ID is required")
    private String garageId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Standard time is required")
    @Positive(message = "Standard time must be positive")
    private Integer standardTime;

    @NotEmpty(message = "Pricing list cannot be empty")
    private List<ServiceItem.Pricing> pricing;

    private boolean isActive = true;
}
