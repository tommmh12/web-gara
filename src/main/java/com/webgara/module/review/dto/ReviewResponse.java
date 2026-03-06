package com.webgara.module.review.dto;

import com.webgara.module.review.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private String id;

    private String appointmentId;

    private String customerId;

    private String garageId;

    private String technicianId;

    private Integer overallRating;

    private Review.Ratings ratings;

    private String comment;

    private List<String> media;

    private Review.Reply reply;

    private Boolean isVisible;

    private BigDecimal garageAverageRating;

    private BigDecimal technicianAverageRating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
