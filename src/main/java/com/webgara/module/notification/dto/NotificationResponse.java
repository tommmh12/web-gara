package com.webgara.module.notification.dto;

import com.webgara.module.notification.model.NotificationEvent;
import com.webgara.module.notification.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    private String id;
    
    private String userId;
    
    private String garageId;
    
    private NotificationType type;
    
    private NotificationEvent event;
    
    private String title;
    
    private String message;
    
    private String recipient;
    
    private String referenceType;
    
    private String referenceId;
    
    private boolean isRead;
    
    private LocalDateTime readAt;
    
    private LocalDateTime sentAt;
    
    private boolean sentSuccessfully;
    
    private String errorMessage;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
