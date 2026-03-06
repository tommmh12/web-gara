package com.webgara.module.review.service;

import com.webgara.module.review.dto.ReplyDTO;
import com.webgara.module.review.dto.ReviewRequest;
import com.webgara.module.review.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse getById(String id);

    ReviewResponse replyToReview(String reviewId, ReplyDTO reply);

    ReviewResponse toggleVisibility(String reviewId, Boolean isVisible);

    Page<ReviewResponse> listByGarage(String garageId, Boolean isVisible, Pageable pageable);

    Page<ReviewResponse> listByCustomer(String customerId, Pageable pageable);

    Page<ReviewResponse> listByTechnician(String technicianId, Pageable pageable);

    BigDecimal calculateGarageAverageRating(String garageId);

    BigDecimal calculateTechnicianAverageRating(String technicianId);
}
