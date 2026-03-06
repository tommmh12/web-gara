package com.webgara.module.review.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndexes({
        @CompoundIndex(name = "garage_rating_idx", def = "{'garageId': 1, 'overallRating': -1}"),
})
public class Review {

    @Id
    private String id;

    @Indexed(unique = true)
    private String appointmentId;

    @Indexed
    private String customerId;

    @Indexed
    private String garageId;

    @Indexed
    private String technicianId;

    private Integer overallRating;

    private Ratings ratings;

    private String comment;

    private List<String> media;

    private Reply reply;

    @Builder.Default
    private Boolean isVisible = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Ratings {
        private Integer serviceQuality;
        private Integer technicianSkill;
        private Integer waitingTime;
        private Integer pricing;
        private Integer cleanliness;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Reply {
        private String content;
        private String repliedBy;
        private LocalDateTime repliedAt;
    }
}
