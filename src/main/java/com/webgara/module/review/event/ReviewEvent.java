package com.webgara.module.review.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvent {

    public enum EventType {
        CREATED,
        REPLY_ADDED,
        VISIBILITY_CHANGED,
        REPORTED
    }

    private String reviewId;
    private EventType eventType;
    private String triggeredBy;
    private String details;
    private LocalDateTime timestamp;
}
