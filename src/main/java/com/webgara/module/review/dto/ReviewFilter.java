package com.webgara.module.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFilter {

    private String garageId;
    private String customerId;
    private String technicianId;
    private Integer minRating;
    private Integer maxRating;
    private Boolean isVisible;
    private Boolean hasReply;
    private String sortBy;
    private String sortDirection;
}
