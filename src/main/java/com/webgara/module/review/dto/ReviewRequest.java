package com.webgara.module.review.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "Appointment ID is required")
    private String appointmentId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Garage ID is required")
    private String garageId;

    private String technicianId;

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Overall rating must be between 1 and 5")
    @Max(value = 5, message = "Overall rating must be between 1 and 5")
    private Integer overallRating;

    @NotNull(message = "Ratings are required")
    @Valid
    private RatingsDTO ratings;

    private String comment;

    private List<String> media;
}
