package com.webgara.module.notification.service;

import com.webgara.module.notification.dto.NotificationRequest;
import com.webgara.module.notification.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    
    NotificationResponse create(NotificationRequest request);
    
    NotificationResponse getById(String id);
    
    NotificationResponse markAsRead(String id);
    
    Page<NotificationResponse> getByUserId(String userId, Pageable pageable);
    
    Page<NotificationResponse> getUnreadByUserId(String userId, Pageable pageable);
    
    long countUnreadByUserId(String userId);
    
    Page<NotificationResponse> getByGarageId(String garageId, Pageable pageable);
    
    void delete(String id);
}
