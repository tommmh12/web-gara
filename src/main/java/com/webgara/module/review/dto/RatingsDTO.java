package com.webgara.module.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingsDTO {

    @NotNull(message = "Service quality rating is required")
    @Min(value = 1, message = "Service quality rating must be between 1 and 5")
    @Max(value = 5, message = "Service quality rating must be between 1 and 5")
    private Integer serviceQuality;

    @NotNull(message = "Technician skill rating is required")
    @Min(value = 1, message = "Technician skill rating must be between 1 and 5")
    @Max(value = 5, message = "Technician skill rating must be between 1 and 5")
    private Integer technicianSkill;

    @NotNull(message = "Waiting time rating is required")
    @Min(value = 1, message = "Waiting time rating must be between 1 and 5")
    @Max(value = 5, message = "Waiting time rating must be between 1 and 5")
    private Integer waitingTime;

    @NotNull(message = "Pricing rating is required")
    @Min(value = 1, message = "Pricing rating must be between 1 and 5")
    @Max(value = 5, message = "Pricing rating must be between 1 and 5")
    private Integer pricing;

    @NotNull(message = "Cleanliness rating is required")
    @Min(value = 1, message = "Cleanliness rating must be between 1 and 5")
    @Max(value = 5, message = "Cleanliness rating must be between 1 and 5")
    private Integer cleanliness;
}
