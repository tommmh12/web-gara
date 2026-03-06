package com.webgara.module.review.repository;

import com.webgara.module.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    Optional<Review> findByAppointmentId(String appointmentId);

    Page<Review> findByGarageIdAndIsVisibleOrderByOverallRatingDesc(
            String garageId,
            Boolean isVisible,
            Pageable pageable
    );

    Page<Review> findByCustomerId(String customerId, Pageable pageable);

    Page<Review> findByTechnicianId(String technicianId, Pageable pageable);

    List<Review> findByGarageId(String garageId);

    List<Review> findByTechnicianIdAndIsVisible(String technicianId, Boolean isVisible);
}
