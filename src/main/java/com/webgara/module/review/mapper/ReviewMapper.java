package com.webgara.module.review.mapper;

import com.webgara.module.review.dto.ReviewRequest;
import com.webgara.module.review.dto.ReviewResponse;
import com.webgara.module.review.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reply", ignore = true)
    @Mapping(target = "isVisible", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewRequest request);

    ReviewResponse toResponse(Review review);
}
