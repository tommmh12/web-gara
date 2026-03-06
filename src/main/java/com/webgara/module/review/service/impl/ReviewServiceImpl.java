package com.webgara.module.review.service.impl;

import com.webgara.common.exception.ConflictException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.review.dto.ReplyDTO;
import com.webgara.module.review.dto.ReviewRequest;
import com.webgara.module.review.dto.ReviewResponse;
import com.webgara.module.review.mapper.ReviewMapper;
import com.webgara.module.review.model.Review;
import com.webgara.module.review.repository.ReviewRepository;
import com.webgara.module.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        // Check if review already exists for this appointment
        reviewRepository.findByAppointmentId(request.getAppointmentId())
                .ifPresent(review -> {
                    throw new ConflictException("Review already exists for this appointment");
                });

        // Create review
        Review review = reviewMapper.toEntity(request);
        review.setIsVisible(true);
        review.setRatings(Review.Ratings.builder()
                .serviceQuality(request.getRatings().getServiceQuality())
                .technicianSkill(request.getRatings().getTechnicianSkill())
                .waitingTime(request.getRatings().getWaitingTime())
                .pricing(request.getRatings().getPricing())
                .cleanliness(request.getRatings().getCleanliness())
                .build());

        Review savedReview = reviewRepository.save(review);
        ReviewResponse response = reviewMapper.toResponse(savedReview);
        response.setGarageAverageRating(calculateGarageAverageRating(request.getGarageId()));
        if (request.getTechnicianId() != null) {
            response.setTechnicianAverageRating(calculateTechnicianAverageRating(request.getTechnicianId()));
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getById(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        ReviewResponse response = reviewMapper.toResponse(review);
        response.setGarageAverageRating(calculateGarageAverageRating(review.getGarageId()));
        if (review.getTechnicianId() != null) {
            response.setTechnicianAverageRating(calculateTechnicianAverageRating(review.getTechnicianId()));
        }

        return response;
    }

    @Override
    public ReviewResponse replyToReview(String reviewId, ReplyDTO reply) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        review.setReply(Review.Reply.builder()
                .content(reply.getContent())
                .repliedBy(reply.getRepliedBy())
                .repliedAt(LocalDateTime.now())
                .build());

        Review updatedReview = reviewRepository.save(review);
        ReviewResponse response = reviewMapper.toResponse(updatedReview);
        response.setGarageAverageRating(calculateGarageAverageRating(review.getGarageId()));
        if (review.getTechnicianId() != null) {
            response.setTechnicianAverageRating(calculateTechnicianAverageRating(review.getTechnicianId()));
        }

        return response;
    }

    @Override
    public ReviewResponse toggleVisibility(String reviewId, Boolean isVisible) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        review.setIsVisible(isVisible);
        Review updatedReview = reviewRepository.save(review);
        ReviewResponse response = reviewMapper.toResponse(updatedReview);
        response.setGarageAverageRating(calculateGarageAverageRating(review.getGarageId()));
        if (review.getTechnicianId() != null) {
            response.setTechnicianAverageRating(calculateTechnicianAverageRating(review.getTechnicianId()));
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> listByGarage(String garageId, Boolean isVisible, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByGarageIdAndIsVisibleOrderByOverallRatingDesc(
                garageId,
                isVisible,
                pageable
        );

        List<ReviewResponse> responses = reviews.getContent().stream()
                .map(review -> {
                    ReviewResponse response = reviewMapper.toResponse(review);
                    response.setGarageAverageRating(calculateGarageAverageRating(garageId));
                    if (review.getTechnicianId() != null) {
                        response.setTechnicianAverageRating(calculateTechnicianAverageRating(review.getTechnicianId()));
                    }
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, reviews.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> listByCustomer(String customerId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByCustomerId(customerId, pageable);

        List<ReviewResponse> responses = reviews.getContent().stream()
                .map(review -> {
                    ReviewResponse response = reviewMapper.toResponse(review);
                    response.setGarageAverageRating(calculateGarageAverageRating(review.getGarageId()));
                    if (review.getTechnicianId() != null) {
                        response.setTechnicianAverageRating(calculateTechnicianAverageRating(review.getTechnicianId()));
                    }
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, reviews.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> listByTechnician(String technicianId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByTechnicianId(technicianId, pageable);

        List<ReviewResponse> responses = reviews.getContent().stream()
                .map(review -> {
                    ReviewResponse response = reviewMapper.toResponse(review);
                    response.setGarageAverageRating(calculateGarageAverageRating(review.getGarageId()));
                    response.setTechnicianAverageRating(calculateTechnicianAverageRating(technicianId));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, reviews.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateGarageAverageRating(String garageId) {
        List<Review> reviews = reviewRepository.findByGarageId(garageId);

        if (reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double average = reviews.stream()
                .mapToInt(Review::getOverallRating)
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(average)
                .setScale(1, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTechnicianAverageRating(String technicianId) {
        List<Review> reviews = reviewRepository.findByTechnicianIdAndIsVisible(technicianId, true);

        if (reviews.isEmpty()) {
            return BigDecimal.ZERO;
        }

        double average = reviews.stream()
                .mapToInt(Review::getOverallRating)
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(average)
                .setScale(1, RoundingMode.HALF_UP);
    }
}
