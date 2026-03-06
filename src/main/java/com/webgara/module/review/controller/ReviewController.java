package com.webgara.module.review.controller;

import com.webgara.common.dto.ApiResponse;
import com.webgara.common.dto.PageResponse;
import com.webgara.module.review.dto.ReplyDTO;
import com.webgara.module.review.dto.ReviewRequest;
import com.webgara.module.review.dto.ReviewResponse;
import com.webgara.module.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(@PathVariable String id) {
        ReviewResponse response = reviewService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> replyToReview(
            @PathVariable String id,
            @Valid @RequestBody ReplyDTO reply) {
        ReviewResponse response = reviewService.replyToReview(id, reply);
        return ResponseEntity.ok(ApiResponse.success(response, "Reply added successfully"));
    }

    @PatchMapping("/{id}/visibility")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> toggleVisibility(
            @PathVariable String id,
            @RequestParam Boolean isVisible) {
        ReviewResponse response = reviewService.toggleVisibility(id, isVisible);
        return ResponseEntity.ok(ApiResponse.success(response, "Review visibility toggled successfully"));
    }

    @GetMapping("/garage/{garageId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> listGarageReviews(
            @PathVariable String garageId,
            @RequestParam(defaultValue = "true") Boolean isVisible,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewResponse> reviews = reviewService.listByGarage(garageId, isVisible, pageable);
        PageResponse<ReviewResponse> pageResponse = PageResponse.<ReviewResponse>builder()
                .content(reviews.getContent())
                .pageNo(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .last(reviews.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> listMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        String customerId = getCurrentUserId();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewResponse> reviews = reviewService.listByCustomer(customerId, pageable);
        PageResponse<ReviewResponse> pageResponse = PageResponse.<ReviewResponse>builder()
                .content(reviews.getContent())
                .pageNo(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .last(reviews.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> listTechnicianReviews(
            @PathVariable String technicianId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReviewResponse> reviews = reviewService.listByTechnician(technicianId, pageable);
        PageResponse<ReviewResponse> pageResponse = PageResponse.<ReviewResponse>builder()
                .content(reviews.getContent())
                .pageNo(reviews.getNumber())
                .pageSize(reviews.getSize())
                .totalElements(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .last(reviews.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    @GetMapping("/garage/{garageId}/average")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGarageAverageRating(
            @PathVariable String garageId) {
        BigDecimal averageRating = reviewService.calculateGarageAverageRating(garageId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("garageId", garageId);
        data.put("averageRating", averageRating);
        data.put("totalReviews", getTotalReviewsForGarage(garageId));

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/technician/{technicianId}/average")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTechnicianAverageRating(
            @PathVariable String technicianId) {
        BigDecimal averageRating = reviewService.calculateTechnicianAverageRating(technicianId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("technicianId", technicianId);
        data.put("averageRating", averageRating);
        data.put("totalReviews", getTotalReviewsForTechnician(technicianId));

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // Helper method to get current user ID
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }

    // Helper method to get total reviews count for garage
    private long getTotalReviewsForGarage(String garageId) {
        Page<ReviewResponse> reviews = reviewService.listByGarage(garageId, true, PageRequest.of(0, 1));
        return reviews.getTotalElements();
    }

    // Helper method to get total reviews count for technician
    private long getTotalReviewsForTechnician(String technicianId) {
        Page<ReviewResponse> reviews = reviewService.listByTechnician(technicianId, PageRequest.of(0, 1));
        return reviews.getTotalElements();
    }
}
