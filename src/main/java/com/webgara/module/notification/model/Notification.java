package com.webgara.module.notification.model;

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

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "event_type_idx", def = "{'event': 1, 'type': 1}"),
        @CompoundIndex(name = "unread_user_idx", def = "{'userId': 1, 'isRead': 1}")
})
public class Notification {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String garageId;
    
    private NotificationType type;
    
    private NotificationEvent event;
    
    private String title;
    
    private String message;
    
    private String recipient;
    
    private String referenceType;
    
    private String referenceId;
    
    @Builder.Default
    private boolean isRead = false;
    
    private LocalDateTime readAt;
    
    private LocalDateTime sentAt;
    
    private boolean sentSuccessfully;
    
    private String errorMessage;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
