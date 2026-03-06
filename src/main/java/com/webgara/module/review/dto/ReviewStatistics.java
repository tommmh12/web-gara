package com.webgara.module.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatistics {

    private String entityId;
    private String entityType;
    private BigDecimal averageRating;
    private Long totalReviews;
    private Long visibleReviews;

    public enum EntityType {
        GARAGE,
        TECHNICIAN
    }
}
