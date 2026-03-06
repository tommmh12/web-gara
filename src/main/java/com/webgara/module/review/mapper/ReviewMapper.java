package com.webgara.module.review.mapper;

import com.webgara.module.review.dto.ReviewRequest;
import com.webgara.module.review.dto.ReviewResponse;
import com.webgara.module.review.model.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review toEntity(ReviewRequest request);

    ReviewResponse toResponse(Review review);
}
