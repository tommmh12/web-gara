package com.webgara.module.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompatibleVehiclesDTO {
    @NotBlank(message = "Vehicle brand is required")
    private String brand;

    @NotEmpty(message = "Models list cannot be empty")
    private List<String> models;
}
